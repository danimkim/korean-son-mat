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

    /** An alternative ingredient and the dietary restrictions it helps satisfy. */
    public record Substitute(String name, Set<DietaryTag> dietaryTags) {
    }

    /** A UK-available alternative for a hard-to-find Korean pantry ingredient. */
    public record UKSubstitute(String name) {
    }

    /**
     * A single ingredient line, annotated with whether the user has it on hand,
     * dietary substitutes, and UK-availability substitutes shown separately.
     */
    public record IngredientView(
            String name,
            String quantity,
            boolean available,
            List<Substitute> substitutes,
            List<UKSubstitute> ukSubstitutes
    ) {
    }

    /** One ingredient swap, e.g. "Beef sirloin" → "Plant-based beef strips". */
    public record Swap(String from, String to) {
    }

    /**
     * Suggested swaps that move a recipe toward a dietary restriction it does not
     * already satisfy, e.g. tag=VEGAN with swaps [Beef sirloin → Plant-based beef].
     */
    public record DietAdaptation(DietaryTag tag, List<Swap> swaps) {
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
            int matchedIngredients,
            List<DietAdaptation> adaptations
    ) {
    }
}
