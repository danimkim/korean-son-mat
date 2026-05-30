package com.koreansonmat.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koreansonmat.model.DietaryTag;
import com.koreansonmat.model.Ingredient;
import com.koreansonmat.model.Recipe;
import com.koreansonmat.service.RecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * AI agent pipeline that turns a recipe web page into structured, database-ready
 * data:
 *
 * <pre>
 *   fetch(url) → extract page text → Claude (structure to JSON) → ScrapedRecipe → persist
 * </pre>
 *
 * <p>The agent calls the Anthropic Messages API when {@code ANTHROPIC_API_KEY} is
 * configured. Without a key it is inert by design — the curated seed catalog in
 * {@code DataSeeder} stands in for scraped content so the app runs offline.
 *
 * <p>Recipes are validated to be Korean cuisine before they are stored, honoring
 * the product constraint that the catalog is exclusively Korean.
 */
@Component
public class RecipeScraperAgent {

    private static final Logger log = LoggerFactory.getLogger(RecipeScraperAgent.class);
    private static final String ANTHROPIC_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-opus-4-8";

    private final RecipeService recipeService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    @Value("${anthropic.api-key:${ANTHROPIC_API_KEY:}}")
    private String anthropicApiKey;

    public RecipeScraperAgent(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public boolean isEnabled() {
        return anthropicApiKey != null && !anthropicApiKey.isBlank();
    }

    /**
     * Scrape a single recipe page and persist it. Returns the saved recipe, or
     * empty if the agent is disabled or the page could not be structured.
     */
    public Recipe scrapeAndStore(String url) {
        ScrapedRecipe scraped = scrape(url);
        if (scraped == null) {
            return null;
        }
        return recipeService.save(toEntity(scraped));
    }

    /** Fetch and structure a recipe page without persisting it. */
    public ScrapedRecipe scrape(String url) {
        if (!isEnabled()) {
            log.info("RecipeScraperAgent disabled (no ANTHROPIC_API_KEY); skipping scrape of {}", url);
            return null;
        }
        try {
            String pageText = fetchPageText(url);
            String json = structureWithClaude(pageText, url);
            return parse(json);
        } catch (Exception e) {
            log.warn("Failed to scrape recipe from {}: {}", url, e.getMessage());
            return null;
        }
    }

    private String fetchPageText(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("User-Agent", "korean-son-mat-recipe-agent/0.1")
                .GET()
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        // Strip tags so the model sees mostly readable text and fewer tokens.
        return response.body().replaceAll("(?s)<script.*?</script>", " ")
                .replaceAll("(?s)<style.*?</style>", " ")
                .replaceAll("<[^>]+>", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String structureWithClaude(String pageText, String sourceUrl) throws Exception {
        String prompt = """
                You are a recipe extraction agent for a KOREAN-ONLY recipe app.
                From the page text below, extract a single Korean recipe as JSON with keys:
                name, koreanName, description, thumbnailUrl, difficulty (Easy|Medium|Hard),
                cookTimeMinutes (int), servings (int),
                ingredients (array of {name, quantity}),
                steps (array of strings),
                dietaryTags (subset of VEGAN, VEGETARIAN, GLUTEN_FREE, DAIRY_FREE, PESCATARIAN).
                If the dish is NOT Korean cuisine, return {"name": null}.
                Respond with ONLY the JSON object, no prose.

                SOURCE_URL: %s
                PAGE_TEXT:
                %s
                """.formatted(sourceUrl, truncate(pageText, 12000));

        String body = objectMapper.writeValueAsString(objectMapper.createObjectNode()
                .put("model", MODEL)
                .put("max_tokens", 2000)
                .set("messages", objectMapper.createArrayNode()
                        .add(objectMapper.createObjectNode()
                                .put("role", "user")
                                .put("content", prompt))));

        HttpRequest request = HttpRequest.newBuilder(URI.create(ANTHROPIC_URL))
                .timeout(Duration.ofSeconds(60))
                .header("x-api-key", anthropicApiKey)
                .header("anthropic-version", "2023-06-01")
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode root = objectMapper.readTree(response.body());
        // Messages API returns { content: [ { type: "text", text: "..." } ] }
        return root.path("content").path(0).path("text").asText();
    }

    private ScrapedRecipe parse(String json) throws Exception {
        JsonNode node = objectMapper.readTree(json);
        if (node.path("name").isNull() || node.path("name").asText().isBlank()) {
            return null; // not a Korean recipe
        }
        List<ScrapedRecipe.IngredientLine> ingredients = new ArrayList<>();
        for (JsonNode i : node.path("ingredients")) {
            ingredients.add(new ScrapedRecipe.IngredientLine(
                    i.path("name").asText(), i.path("quantity").asText("")));
        }
        List<String> steps = new ArrayList<>();
        node.path("steps").forEach(s -> steps.add(s.asText()));
        Set<DietaryTag> tags = EnumSet.noneOf(DietaryTag.class);
        node.path("dietaryTags").forEach(t -> {
            try {
                tags.add(DietaryTag.valueOf(t.asText()));
            } catch (IllegalArgumentException ignored) {
                // skip unknown tags
            }
        });
        return new ScrapedRecipe(
                node.path("name").asText(),
                node.path("koreanName").asText(""),
                node.path("description").asText(""),
                node.path("thumbnailUrl").asText(""),
                node.path("difficulty").asText("Medium"),
                node.path("cookTimeMinutes").asInt(0),
                node.path("servings").asInt(0),
                ingredients,
                steps,
                tags);
    }

    private Recipe toEntity(ScrapedRecipe s) {
        List<Ingredient> ingredients = s.ingredients().stream()
                .map(i -> new Ingredient(i.name(), i.quantity()))
                .toList();
        return new Recipe(s.name(), s.koreanName(), s.description(), s.thumbnailUrl(),
                s.difficulty(), s.cookTimeMinutes(), s.servings(),
                new ArrayList<>(ingredients), new ArrayList<>(s.steps()), s.dietaryTags());
    }

    private String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max);
    }
}
