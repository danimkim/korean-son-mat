package com.koreansonmat.config;

import com.koreansonmat.dto.RecipeDtos.UKSubstitute;
import com.koreansonmat.model.Ingredient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Curated mapping of ingredient → UK-available alternatives for Korean pantry
 * items that are hard to find in mainstream UK supermarkets (Tesco, Sainsbury's,
 * ASDA). Keys are normalized ingredient names matching those used in {@link DataSeeder}.
 */
@Component
public class UKIngredientSubstitutions {

    private final Map<String, List<UKSubstitute>> byIngredient = buildMap();

    /** UK substitutes for the given ingredient, or an empty list if none are known. */
    public List<UKSubstitute> forIngredient(String ingredientName) {
        return byIngredient.getOrDefault(Ingredient.normalize(ingredientName), List.of());
    }

    private Map<String, List<UKSubstitute>> buildMap() {
        return Map.ofEntries(
                entry("Gochugaru",
                        sub("Kashmiri chili powder"),
                        sub("Regular chili flakes")),
                entry("Doenjang",
                        sub("White miso paste"),
                        sub("Yellow miso paste")),
                entry("Sweet potato noodles",
                        sub("Mung bean glass noodles"),
                        sub("Rice vermicelli")),
                entry("Rice cakes",
                        sub("Gnocchi")),
                entry("Ginseng",
                        sub("Ginseng tea bags (steep and add to broth)")),
                entry("Jujube",
                        sub("Medjool dates")),
                entry("Soft tofu",
                        sub("Silken tofu (Clearspring / Cauldron, sold at major supermarkets)")),
                entry("Glutinous rice",
                        sub("Pudding rice"),
                        sub("Arborio rice")),
                entry("Fish cake",
                        sub("British fish cake, roughly chopped")),
                entry("Soybeans",
                        sub("Frozen shelled edamame beans"))
        );
    }

    private Map.Entry<String, List<UKSubstitute>> entry(String ingredient, UKSubstitute... subs) {
        return Map.entry(Ingredient.normalize(ingredient), List.of(subs));
    }

    private UKSubstitute sub(String name) {
        return new UKSubstitute(name);
    }
}
