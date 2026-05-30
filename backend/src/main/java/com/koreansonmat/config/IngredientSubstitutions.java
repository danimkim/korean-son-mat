package com.koreansonmat.config;

import com.koreansonmat.dto.RecipeDtos.Substitute;
import com.koreansonmat.model.DietaryTag;
import com.koreansonmat.model.Ingredient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Curated mapping of ingredient → alternative ingredients, each tagged with the
 * dietary restriction(s) the swap helps satisfy. Used to suggest substitutions
 * (e.g. "Beef sirloin" → plant-based beef / tofu so a dish can be made vegan).
 *
 * <p>Keys are normalized ingredient names and must line up with the ingredient
 * names used in {@link DataSeeder}.
 */
@Component
public class IngredientSubstitutions {

    private static final Set<DietaryTag> VEG = Set.of(DietaryTag.VEGAN, DietaryTag.VEGETARIAN);

    private final Map<String, List<Substitute>> byIngredient = buildMap();

    /** Substitutes for the given ingredient, or an empty list if none are known. */
    public List<Substitute> forIngredient(String ingredientName) {
        return byIngredient.getOrDefault(Ingredient.normalize(ingredientName), List.of());
    }

    private Map<String, List<Substitute>> buildMap() {
        return Map.ofEntries(
                // --- Meat & poultry → plant-based (vegan / vegetarian) ---
                entry("Beef sirloin",
                        sub("Plant-based beef strips", VEG),
                        sub("Extra-firm tofu", VEG),
                        sub("King oyster mushroom", VEG)),
                entry("Pork belly",
                        sub("Plant-based pork / soy strips", VEG),
                        sub("Extra-firm tofu", VEG),
                        sub("King oyster mushroom", VEG)),
                entry("Whole chicken",
                        sub("Soy curls", VEG),
                        sub("Seitan", VEG),
                        sub("Extra-firm tofu", VEG)),

                // --- Seafood → mushroom / plant-based ---
                entry("Shrimp",
                        sub("King oyster mushroom", VEG),
                        sub("Hearts of palm", VEG)),
                entry("Squid",
                        sub("King oyster mushroom", VEG),
                        sub("Lion's mane mushroom", VEG)),
                entry("Clams",
                        sub("Diced shiitake mushroom", VEG),
                        sub("Kelp (dasima)", VEG)),
                entry("Fish cake",
                        sub("Fried tofu", VEG),
                        sub("Extra rice cakes", VEG),
                        sub("Mushroom", VEG)),

                // --- Egg → vegan egg / tofu (vegetarian dishes that aren't vegan) ---
                entry("Egg",
                        sub("Mung-bean vegan egg (e.g. JUST Egg)", VEG),
                        sub("Mashed soft tofu", VEG)),

                // --- Fermented items that often contain fish (kimchi) ---
                entry("Kimchi",
                        sub("Vegan kimchi (no fish sauce / saeujeot)", VEG)),

                // --- Gluten-free swaps ---
                entry("Soy sauce",
                        sub("Tamari (gluten-free)", Set.of(DietaryTag.GLUTEN_FREE))),
                entry("Flour",
                        sub("Rice flour", Set.of(DietaryTag.GLUTEN_FREE))),
                entry("Somen noodles",
                        sub("Rice vermicelli", Set.of(DietaryTag.GLUTEN_FREE))),
                entry("Gochujang",
                        sub("Gluten-free gochujang", Set.of(DietaryTag.GLUTEN_FREE)))
        );
    }

    private Map.Entry<String, List<Substitute>> entry(String ingredient, Substitute... subs) {
        return Map.entry(Ingredient.normalize(ingredient), List.of(subs));
    }

    private Substitute sub(String name, Set<DietaryTag> tags) {
        return new Substitute(name, tags);
    }
}
