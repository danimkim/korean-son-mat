package com.koreansonmat.controller;

import com.koreansonmat.dto.RecipeDtos.RecipeDetail;
import com.koreansonmat.dto.RecipeDtos.RecipeSummary;
import com.koreansonmat.model.DietaryTag;
import com.koreansonmat.model.Recipe;
import com.koreansonmat.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * REST endpoints for browsing and searching Korean recipes.
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    /** List all recipes, or filter via the same query params as {@link #search}. */
    @GetMapping
    public List<RecipeSummary> list(
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String dietary,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer maxCookTime) {
        return recipeService.search(parseCsv(ingredients), parseDietary(dietary),
                Set.copyOf(parseCsv(difficulty)), maxCookTime);
    }

    /**
     * Supercook-style search by available ingredients, dietary restrictions,
     * difficulty, and/or maximum cook time. All params are optional;
     * {@code ingredients}, {@code dietary}, and {@code difficulty} are
     * comma-separated lists, {@code maxCookTime} is minutes.
     */
    @GetMapping("/search")
    public List<RecipeSummary> search(
            @RequestParam(required = false) String ingredients,
            @RequestParam(required = false) String dietary,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer maxCookTime) {
        return recipeService.search(parseCsv(ingredients), parseDietary(dietary),
                Set.copyOf(parseCsv(difficulty)), maxCookTime);
    }

    /** Distinct ingredient names across the catalog, for the ingredient picker. */
    @GetMapping("/ingredients")
    public List<String> ingredients() {
        return recipeService.allIngredientNames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDetail> getById(
            @PathVariable Long id,
            @RequestParam(required = false) String ingredients) {
        return recipeService.findById(id, parseCsv(ingredients))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Recipe> create(@RequestBody Recipe recipe) {
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeService.save(recipe));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return recipeService.deleteById(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    private List<String> parseCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            return Collections.emptyList();
        }
        return List.of(csv.split(",")).stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private Set<DietaryTag> parseDietary(String csv) {
        return parseCsv(csv).stream()
                .map(s -> s.toUpperCase(Locale.ROOT).replace('-', '_'))
                .map(DietaryTag::valueOf)
                .collect(Collectors.toSet());
    }
}
