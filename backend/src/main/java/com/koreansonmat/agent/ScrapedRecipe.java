package com.koreansonmat.agent;

import com.koreansonmat.model.DietaryTag;

import java.util.List;
import java.util.Set;

/**
 * Structured recipe data produced by the scraping agent before it is persisted.
 * Mirrors the shape the LLM is asked to emit (see {@link RecipeScraperAgent}).
 */
public record ScrapedRecipe(
        String name,
        String koreanName,
        String description,
        String thumbnailUrl,
        String difficulty,
        Integer cookTimeMinutes,
        Integer servings,
        List<IngredientLine> ingredients,
        List<String> steps,
        Set<DietaryTag> dietaryTags
) {
    public record IngredientLine(String name, String quantity) {
    }
}
