package com.koreansonmat.dto;

import com.koreansonmat.model.DietaryTag;

import java.util.List;
import java.util.Set;

/**
 * Response payloads returned by the REST API. Kept as records to keep the
 * controller layer decoupled from JPA entities.
 */
public final class RecipeDtos {

    private RecipeDtos() {
    }

    /** A single ingredient line, annotated with whether the user has it on hand. */
    public record IngredientView(String name, String quantity, boolean available) {
    }

    /** Lightweight shape used by the recipe list / cards. */
    public record RecipeSummary(
            Long id,
            String name,
            String koreanName,
            String description,
            String thumbnailUrl,
            Integer cookTimeMinutes,
            String difficulty,
            Set<DietaryTag> dietaryTags,
            int totalIngredients,
            int matchedIngredients,
            List<String> matchedIngredientNames,
            List<String> missingIngredientNames
    ) {
    }

    /** Full recipe shape used by the detail screen. */
    public record RecipeDetail(
            Long id,
            String name,
            String koreanName,
            String description,
            String thumbnailUrl,
            String difficulty,
            Integer cookTimeMinutes,
            Integer servings,
            Set<DietaryTag> dietaryTags,
            List<IngredientView> ingredients,
            List<String> steps,
            int matchedIngredients
    ) {
    }
}
