package com.koreansonmat.service;

import com.koreansonmat.dto.RecipeDtos.IngredientView;
import com.koreansonmat.dto.RecipeDtos.RecipeDetail;
import com.koreansonmat.dto.RecipeDtos.RecipeSummary;
import com.koreansonmat.model.DietaryTag;
import com.koreansonmat.model.Ingredient;
import com.koreansonmat.model.Recipe;
import com.koreansonmat.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Business logic for discovering recipes by available ingredients and dietary
 * restrictions, modelled after Supercook's ingredient-first flow.
 */
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Search recipes.
     *
     * @param ingredientNames ingredients the user has on hand (may be empty)
     * @param dietaryTags     dietary restrictions every result must satisfy (may be empty)
     * @return recipe summaries; when ingredients are supplied, only recipes that
     *         match at least one are returned, ranked by match count descending.
     */
    public List<RecipeSummary> search(List<String> ingredientNames, Set<DietaryTag> dietaryTags) {
        Set<String> normalizedPantry = ingredientNames.stream()
                .map(Ingredient::normalize)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());

        return recipeRepository.findAll().stream()
                .filter(recipe -> matchesDietary(recipe, dietaryTags))
                .map(recipe -> toSummary(recipe, normalizedPantry))
                .filter(summary -> normalizedPantry.isEmpty() || summary.matchedIngredients() > 0)
                .sorted(Comparator
                        .comparingInt(RecipeSummary::matchedIngredients).reversed()
                        .thenComparing(RecipeSummary::name))
                .collect(Collectors.toList());
    }

    public List<RecipeSummary> findAll() {
        return search(List.of(), Set.of());
    }

    public Optional<RecipeDetail> findById(Long id, List<String> ingredientNames) {
        Set<String> normalizedPantry = ingredientNames.stream()
                .map(Ingredient::normalize)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
        return recipeRepository.findById(id).map(recipe -> toDetail(recipe, normalizedPantry));
    }

    public Recipe save(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public boolean deleteById(Long id) {
        if (recipeRepository.existsById(id)) {
            recipeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /** Distinct, sorted list of every ingredient known to the catalog (for autocomplete). */
    public List<String> allIngredientNames() {
        return recipeRepository.findAll().stream()
                .flatMap(r -> r.getIngredients().stream())
                .map(Ingredient::getName)
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }

    private boolean matchesDietary(Recipe recipe, Set<DietaryTag> required) {
        return required.isEmpty() || recipe.getDietaryTags().containsAll(required);
    }

    private RecipeSummary toSummary(Recipe recipe, Set<String> pantry) {
        List<String> matched = new ArrayList<>();
        List<String> missing = new ArrayList<>();
        for (Ingredient ingredient : recipe.getIngredients()) {
            if (pantry.contains(ingredient.getNormalizedName())) {
                matched.add(ingredient.getName());
            } else {
                missing.add(ingredient.getName());
            }
        }
        return new RecipeSummary(
                recipe.getId(),
                recipe.getName(),
                recipe.getKoreanName(),
                recipe.getDescription(),
                recipe.getThumbnailUrl(),
                recipe.getCookTimeMinutes(),
                recipe.getDifficulty(),
                recipe.getDietaryTags(),
                recipe.getIngredients().size(),
                matched.size(),
                matched,
                missing
        );
    }

    private RecipeDetail toDetail(Recipe recipe, Set<String> pantry) {
        List<IngredientView> ingredientViews = recipe.getIngredients().stream()
                .map(i -> new IngredientView(
                        i.getName(),
                        i.getQuantity(),
                        pantry.contains(i.getNormalizedName())))
                .collect(Collectors.toList());
        int matched = (int) ingredientViews.stream().filter(IngredientView::available).count();
        return new RecipeDetail(
                recipe.getId(),
                recipe.getName(),
                recipe.getKoreanName(),
                recipe.getDescription(),
                recipe.getThumbnailUrl(),
                recipe.getDifficulty(),
                recipe.getCookTimeMinutes(),
                recipe.getServings(),
                recipe.getDietaryTags(),
                ingredientViews,
                recipe.getSteps(),
                matched
        );
    }
}
