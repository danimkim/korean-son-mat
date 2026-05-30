package com.koreansonmat.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * A single ingredient line within a recipe, e.g. "soy sauce" / "2 tbsp".
 *
 * <p>{@code normalizedName} is a lower-cased, trimmed form of {@code name} used
 * for ingredient-based matching so that "Soy Sauce" and "soy sauce" compare equal.
 */
@Embeddable
public class Ingredient {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String normalizedName;

    private String quantity;

    protected Ingredient() {
        // for JPA
    }

    public Ingredient(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
        this.normalizedName = normalize(name);
    }

    public static String normalize(String raw) {
        return raw == null ? "" : raw.trim().toLowerCase();
    }

    public String getName() {
        return name;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public String getQuantity() {
        return quantity;
    }
}
