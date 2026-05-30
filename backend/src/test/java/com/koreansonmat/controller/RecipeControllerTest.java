package com.koreansonmat.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listReturnsRecipes() throws Exception {
        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void searchByDietaryFiltersResults() throws Exception {
        mockMvc.perform(get("/api/recipes/search").param("dietary", "vegan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dietaryTags").exists());
    }

    @Test
    void getByIdReturnsDetail() throws Exception {
        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.steps").isArray());
    }

    @Test
    void unknownRecipeReturns404() throws Exception {
        mockMvc.perform(get("/api/recipes/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void ingredientsEndpointReturnsList() throws Exception {
        mockMvc.perform(get("/api/recipes/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
