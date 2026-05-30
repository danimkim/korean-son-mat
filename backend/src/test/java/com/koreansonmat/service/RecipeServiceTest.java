package com.koreansonmat.service;

import com.koreansonmat.dto.RecipeDtos.RecipeSummary;
import com.koreansonmat.model.DietaryTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Exercises the ingredient and dietary filtering against the seeded catalog.
 */
@SpringBootTest
class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Test
    void findAllReturnsSeededCatalog() {
        assertFalse(recipeService.findAll().isEmpty(), "seed data should be present");
    }

    @Test
    void searchByIngredientsRanksByMatchCount() {
        List<RecipeSummary> results = recipeService.search(
                List.of("kimchi", "rice", "egg"), Set.of());

        assertFalse(results.isEmpty());
        // Every returned recipe must match at least one pantry ingredient.
        assertTrue(results.stream().allMatch(r -> r.matchedIngredients() > 0));
        // Results are sorted by match count descending.
        for (int i = 1; i < results.size(); i++) {
            assertTrue(results.get(i - 1).matchedIngredients() >= results.get(i).matchedIngredients());
        }
    }

    @Test
    void searchByDietaryReturnsOnlyMatchingRecipes() {
        List<RecipeSummary> vegan = recipeService.search(List.of(), Set.of(DietaryTag.VEGAN));

        assertFalse(vegan.isEmpty());
        assertTrue(vegan.stream().allMatch(r -> r.dietaryTags().contains(DietaryTag.VEGAN)));
    }

    @Test
    void searchCombinesIngredientAndDietaryFilters() {
        List<RecipeSummary> results = recipeService.search(
                List.of("spinach", "garlic"), Set.of(DietaryTag.VEGAN));

        assertTrue(results.stream().allMatch(r ->
                r.dietaryTags().contains(DietaryTag.VEGAN) && r.matchedIngredients() > 0));
    }

    @Test
    void emptySearchReturnsEverything() {
        assertEquals(recipeService.findAll().size(),
                recipeService.search(List.of(), Set.of()).size());
    }

    @Test
    void beefRecipeSuggestsVeganSubstituteAndAdaptation() {
        var bulgogi = recipeService.findAll().stream()
                .filter(r -> r.name().equals("Bulgogi"))
                .findFirst().orElseThrow();
        var detail = recipeService.findById(bulgogi.id(), List.of()).orElseThrow();

        // The beef ingredient carries vegan/vegetarian substitutes.
        var beef = detail.ingredients().stream()
                .filter(i -> i.name().equals("Beef sirloin"))
                .findFirst().orElseThrow();
        assertFalse(beef.substitutes().isEmpty(), "beef should have substitutes");
        assertTrue(beef.substitutes().stream()
                .anyMatch(s -> s.dietaryTags().contains(DietaryTag.VEGAN)));

        // A VEGAN adaptation should list a Beef sirloin → ... swap.
        var veganAdaptation = detail.adaptations().stream()
                .filter(a -> a.tag() == DietaryTag.VEGAN)
                .findFirst().orElseThrow();
        assertTrue(veganAdaptation.swaps().stream()
                .anyMatch(s -> s.from().equals("Beef sirloin")));
    }

    @Test
    void alreadyVeganRecipeHasNoVeganAdaptation() {
        var japchae = recipeService.findAll().stream()
                .filter(r -> r.name().equals("Japchae"))
                .findFirst().orElseThrow();
        var detail = recipeService.findById(japchae.id(), List.of()).orElseThrow();

        assertTrue(detail.adaptations().stream().noneMatch(a -> a.tag() == DietaryTag.VEGAN),
                "a vegan recipe should not suggest a vegan adaptation");
    }
}
