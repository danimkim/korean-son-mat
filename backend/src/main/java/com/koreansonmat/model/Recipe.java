package com.koreansonmat.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * A Korean recipe. All recipes in this application are Korean cuisine by design.
 */
@Entity
@Table(name = "recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /** Romanized or Hangul Korean name, e.g. "비빔밥". */
    private String koreanName;

    @Column(length = 1000)
    private String description;

    private String thumbnailUrl;

    private String difficulty;

    private Integer cookTimeMinutes;

    private Integer servings;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "ingredient_order")
    private List<Ingredient> ingredients = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @OrderColumn(name = "step_order")
    @Column(name = "instruction", length = 2000)
    private List<String> steps = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "recipe_dietary_tags", joinColumns = @JoinColumn(name = "recipe_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tag")
    private Set<DietaryTag> dietaryTags = EnumSet.noneOf(DietaryTag.class);

    protected Recipe() {
        // for JPA
    }

    public Recipe(String name, String koreanName, String description, String thumbnailUrl,
                  String difficulty, Integer cookTimeMinutes, Integer servings,
                  List<Ingredient> ingredients, List<String> steps, Set<DietaryTag> dietaryTags) {
        this.name = name;
        this.koreanName = koreanName;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.difficulty = difficulty;
        this.cookTimeMinutes = cookTimeMinutes;
        this.servings = servings;
        this.ingredients = ingredients;
        this.steps = steps;
        this.dietaryTags = dietaryTags;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }

    public Integer getServings() {
        return servings;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public List<String> getSteps() {
        return steps;
    }

    public Set<DietaryTag> getDietaryTags() {
        return dietaryTags;
    }
}
