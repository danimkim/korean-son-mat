package com.koreansonmat.config;

import com.koreansonmat.model.DietaryTag;
import com.koreansonmat.model.Ingredient;
import com.koreansonmat.model.Recipe;
import com.koreansonmat.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Seeds the database with a curated catalog of Korean recipes on first start
 * (when the table is empty). The seed keeps the app useful out of the box and
 * offline.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final RecipeRepository recipeRepository;

    public DataSeeder(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void run(String... args) {
        if (recipeRepository.count() > 0) {
            log.info("Recipe catalog already populated ({} recipes); skipping seed.",
                    recipeRepository.count());
            return;
        }
        recipeRepository.saveAll(seedRecipes());
        log.info("Seeded {} Korean recipes.", recipeRepository.count());
    }

    private List<Recipe> seedRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(recipe("Bibimbap", "비빔밥",
                "Warm rice topped with seasoned vegetables, egg, and gochujang, mixed at the table.",
                "Medium", 40, 2,
                List.of(
                        ing("Rice", "2 cups cooked"),
                        ing("Spinach", "1 bunch"),
                        ing("Carrot", "1, julienned"),
                        ing("Zucchini", "1, sliced"),
                        ing("Egg", "2"),
                        ing("Gochujang", "2 tbsp"),
                        ing("Sesame oil", "1 tbsp"),
                        ing("Soy sauce", "1 tbsp"),
                        ing("Garlic", "2 cloves")),
                List.of(
                        "Cook and season each vegetable separately with a little salt, garlic, and sesame oil.",
                        "Fry the eggs sunny-side up.",
                        "Place warm rice in a bowl and arrange the vegetables on top.",
                        "Add a fried egg and a spoon of gochujang, then mix well before eating."),
                tags(DietaryTag.VEGETARIAN)));

        recipes.add(recipe("Japchae", "잡채",
                "Stir-fried sweet potato glass noodles with vegetables in a savory-sweet sauce.",
                "Medium", 45, 4,
                List.of(
                        ing("Sweet potato noodles", "200g"),
                        ing("Spinach", "1 bunch"),
                        ing("Carrot", "1, julienned"),
                        ing("Onion", "1, sliced"),
                        ing("Shiitake mushroom", "4"),
                        ing("Soy sauce", "3 tbsp"),
                        ing("Sesame oil", "2 tbsp"),
                        ing("Sugar", "1 tbsp"),
                        ing("Garlic", "2 cloves")),
                List.of(
                        "Boil the glass noodles until chewy, drain, and cut roughly.",
                        "Stir-fry each vegetable separately until just tender.",
                        "Combine noodles and vegetables, add soy sauce, sugar, and sesame oil.",
                        "Toss over low heat until glossy and serve warm or at room temperature."),
                tags(DietaryTag.VEGAN, DietaryTag.VEGETARIAN, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Kimchi Jjigae", "김치찌개",
                "A bubbling, spicy stew of aged kimchi and pork — Korean comfort food.",
                "Easy", 30, 3,
                List.of(
                        ing("Kimchi", "2 cups, aged"),
                        ing("Pork belly", "200g"),
                        ing("Tofu", "1/2 block"),
                        ing("Onion", "1/2, sliced"),
                        ing("Green onion", "2 stalks"),
                        ing("Gochugaru", "1 tbsp"),
                        ing("Garlic", "2 cloves"),
                        ing("Sesame oil", "1 tsp")),
                List.of(
                        "Sauté pork and kimchi in sesame oil until fragrant.",
                        "Add water to cover and bring to a boil.",
                        "Stir in gochugaru and garlic; simmer 15 minutes.",
                        "Add tofu and green onion, simmer a few more minutes, and serve hot."),
                tags(DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Tteokbokki", "떡볶이",
                "Chewy rice cakes simmered in a sweet and spicy gochujang sauce.",
                "Easy", 25, 2,
                List.of(
                        ing("Rice cakes", "400g"),
                        ing("Fish cake", "100g"),
                        ing("Gochujang", "2 tbsp"),
                        ing("Gochugaru", "1 tbsp"),
                        ing("Sugar", "1 tbsp"),
                        ing("Soy sauce", "1 tbsp"),
                        ing("Green onion", "2 stalks"),
                        ing("Garlic", "1 clove")),
                List.of(
                        "Soak rice cakes in warm water if hardened.",
                        "Make a sauce with gochujang, gochugaru, sugar, and soy sauce in water.",
                        "Bring to a boil, add rice cakes and fish cake.",
                        "Simmer until the sauce thickens and coats the rice cakes; top with green onion."),
                tags(DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Bulgogi", "불고기",
                "Thinly sliced beef marinated in a sweet soy-garlic sauce, then grilled.",
                "Medium", 60, 4,
                List.of(
                        ing("Beef sirloin", "500g, thinly sliced"),
                        ing("Soy sauce", "4 tbsp"),
                        ing("Sugar", "2 tbsp"),
                        ing("Pear", "1/2, grated"),
                        ing("Garlic", "4 cloves"),
                        ing("Sesame oil", "1 tbsp"),
                        ing("Onion", "1, sliced"),
                        ing("Green onion", "2 stalks")),
                List.of(
                        "Whisk soy sauce, sugar, grated pear, garlic, and sesame oil into a marinade.",
                        "Marinate the sliced beef for at least 30 minutes.",
                        "Sear the beef and onion over high heat until caramelized.",
                        "Garnish with green onion and serve with rice."),
                tags(DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Doenjang Jjigae", "된장찌개",
                "A hearty fermented soybean paste stew loaded with vegetables and tofu.",
                "Easy", 30, 3,
                List.of(
                        ing("Doenjang", "3 tbsp"),
                        ing("Tofu", "1/2 block"),
                        ing("Zucchini", "1/2, sliced"),
                        ing("Potato", "1, cubed"),
                        ing("Onion", "1/2, sliced"),
                        ing("Green chili", "1"),
                        ing("Garlic", "2 cloves"),
                        ing("Green onion", "1 stalk")),
                List.of(
                        "Dissolve doenjang in a pot of water and bring to a boil.",
                        "Add potato and onion; simmer until softened.",
                        "Add zucchini, tofu, and garlic; simmer 10 minutes.",
                        "Finish with green chili and green onion."),
                tags(DietaryTag.VEGETARIAN, DietaryTag.VEGAN, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Sundubu Jjigae", "순두부찌개",
                "Silky soft tofu stew in a spicy, savory broth with seafood.",
                "Medium", 30, 2,
                List.of(
                        ing("Soft tofu", "1 tube"),
                        ing("Clams", "8"),
                        ing("Shrimp", "6"),
                        ing("Gochugaru", "2 tbsp"),
                        ing("Garlic", "2 cloves"),
                        ing("Onion", "1/2, diced"),
                        ing("Egg", "1"),
                        ing("Green onion", "1 stalk"),
                        ing("Sesame oil", "1 tbsp")),
                List.of(
                        "Bloom gochugaru in sesame oil with garlic and onion.",
                        "Add water and bring to a boil; add clams and shrimp.",
                        "Spoon in soft tofu in large curds and simmer gently.",
                        "Crack an egg on top, garnish with green onion, and serve bubbling."),
                tags(DietaryTag.PESCATARIAN, DietaryTag.DAIRY_FREE, DietaryTag.GLUTEN_FREE)));

        recipes.add(recipe("Haemul Pajeon", "해물파전",
                "A crispy pan-fried pancake of green onions and mixed seafood.",
                "Medium", 30, 3,
                List.of(
                        ing("Green onion", "1 bunch"),
                        ing("Squid", "100g"),
                        ing("Shrimp", "100g"),
                        ing("Flour", "1 cup"),
                        ing("Egg", "1"),
                        ing("Water", "3/4 cup"),
                        ing("Soy sauce", "2 tbsp"),
                        ing("Vinegar", "1 tbsp")),
                List.of(
                        "Whisk flour, egg, and water into a batter.",
                        "Lay green onions in a hot oiled pan and pour batter over them.",
                        "Scatter seafood on top and fry until the bottom is crisp.",
                        "Flip, cook through, and serve with a soy-vinegar dipping sauce."),
                tags(DietaryTag.PESCATARIAN, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Gyeran Jjim", "계란찜",
                "Soft, fluffy steamed egg custard, savory and comforting.",
                "Easy", 20, 2,
                List.of(
                        ing("Egg", "4"),
                        ing("Water", "1 cup"),
                        ing("Green onion", "1 stalk"),
                        ing("Salt", "1/2 tsp"),
                        ing("Sesame oil", "1 tsp")),
                List.of(
                        "Beat eggs with water and salt, then strain for smoothness.",
                        "Pour into an earthenware pot and heat over low while stirring.",
                        "Cover and let it puff up and set.",
                        "Drizzle sesame oil and top with green onion."),
                tags(DietaryTag.VEGETARIAN, DietaryTag.GLUTEN_FREE, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Miyeok Guk", "미역국",
                "A nourishing seaweed soup, traditionally eaten on birthdays.",
                "Easy", 35, 4,
                List.of(
                        ing("Dried seaweed", "1 cup, soaked"),
                        ing("Soy sauce", "2 tbsp"),
                        ing("Garlic", "2 cloves"),
                        ing("Sesame oil", "1 tbsp"),
                        ing("Water", "6 cups")),
                List.of(
                        "Soak dried seaweed until soft, then cut into bite-sized pieces.",
                        "Sauté seaweed in sesame oil with garlic.",
                        "Add water and soy sauce; bring to a boil.",
                        "Simmer 25 minutes until the broth is rich and savory."),
                tags(DietaryTag.VEGAN, DietaryTag.VEGETARIAN, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Kongguksu", "콩국수",
                "Chilled noodles in a creamy, nutty soybean broth for hot summer days.",
                "Medium", 40, 2,
                List.of(
                        ing("Soybeans", "1 cup, soaked"),
                        ing("Somen noodles", "200g"),
                        ing("Cucumber", "1/2, julienned"),
                        ing("Salt", "to taste"),
                        ing("Sesame seeds", "1 tbsp")),
                List.of(
                        "Boil soaked soybeans for 10 minutes, then peel and blend with water until smooth.",
                        "Chill the soybean broth thoroughly.",
                        "Boil somen noodles, rinse in cold water, and drain.",
                        "Pour cold broth over the noodles and top with cucumber and sesame seeds."),
                tags(DietaryTag.VEGAN, DietaryTag.VEGETARIAN, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Sigeumchi Namul", "시금치나물",
                "A simple seasoned spinach side dish, fresh and sesame-scented.",
                "Easy", 15, 4,
                List.of(
                        ing("Spinach", "1 bunch"),
                        ing("Garlic", "1 clove"),
                        ing("Soy sauce", "1 tbsp"),
                        ing("Sesame oil", "1 tbsp"),
                        ing("Sesame seeds", "1 tsp")),
                List.of(
                        "Blanch spinach briefly and shock in cold water.",
                        "Squeeze out excess water and cut into manageable lengths.",
                        "Season with garlic, soy sauce, and sesame oil.",
                        "Toss with sesame seeds and serve."),
                tags(DietaryTag.VEGAN, DietaryTag.VEGETARIAN, DietaryTag.GLUTEN_FREE, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Gamja Jorim", "감자조림",
                "Braised potatoes glazed in a sweet and savory soy sauce.",
                "Easy", 25, 4,
                List.of(
                        ing("Potato", "3, cubed"),
                        ing("Soy sauce", "3 tbsp"),
                        ing("Sugar", "1 tbsp"),
                        ing("Garlic", "1 clove"),
                        ing("Sesame oil", "1 tsp"),
                        ing("Sesame seeds", "1 tsp")),
                List.of(
                        "Sauté cubed potatoes in a little oil for a few minutes.",
                        "Add soy sauce, sugar, garlic, and water to cover halfway.",
                        "Simmer until the potatoes are tender and the sauce reduces to a glaze.",
                        "Finish with sesame oil and seeds."),
                tags(DietaryTag.VEGAN, DietaryTag.VEGETARIAN, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Kimchi Bokkeumbap", "김치볶음밥",
                "Quick kimchi fried rice topped with a fried egg.",
                "Easy", 20, 2,
                List.of(
                        ing("Rice", "2 cups cooked"),
                        ing("Kimchi", "1 cup, chopped"),
                        ing("Egg", "2"),
                        ing("Green onion", "2 stalks"),
                        ing("Gochujang", "1 tbsp"),
                        ing("Sesame oil", "1 tbsp"),
                        ing("Garlic", "1 clove")),
                List.of(
                        "Sauté garlic and kimchi in sesame oil until softened.",
                        "Add rice and gochujang; stir-fry until everything is coated and hot.",
                        "Fry the eggs sunny-side up.",
                        "Top each serving with an egg and chopped green onion."),
                tags(DietaryTag.VEGETARIAN, DietaryTag.DAIRY_FREE)));

        recipes.add(recipe("Samgyetang", "삼계탕",
                "A restorative whole-chicken soup stuffed with rice and ginseng.",
                "Hard", 90, 2,
                List.of(
                        ing("Whole chicken", "1 small"),
                        ing("Glutinous rice", "1/2 cup"),
                        ing("Ginseng", "1 root"),
                        ing("Garlic", "6 cloves"),
                        ing("Jujube", "4"),
                        ing("Green onion", "2 stalks"),
                        ing("Salt", "to taste")),
                List.of(
                        "Stuff the cleaned chicken with soaked glutinous rice, garlic, and ginseng.",
                        "Place in a deep pot, cover with water, and add jujube.",
                        "Simmer gently for at least an hour until the chicken is tender.",
                        "Season with salt at the table and garnish with green onion."),
                tags(DietaryTag.GLUTEN_FREE, DietaryTag.DAIRY_FREE)));

        return recipes;
    }

    private Recipe recipe(String name, String koreanName, String description,
                          String difficulty, int cookTime, int servings,
                          List<Ingredient> ingredients, List<String> steps,
                          Set<DietaryTag> dietaryTags) {
        return new Recipe(name, koreanName, description, null, difficulty, cookTime, servings,
                new ArrayList<>(ingredients), new ArrayList<>(steps), dietaryTags);
    }

    private Ingredient ing(String name, String quantity) {
        return new Ingredient(name, quantity);
    }

    private Set<DietaryTag> tags(DietaryTag... tags) {
        Set<DietaryTag> set = EnumSet.noneOf(DietaryTag.class);
        for (DietaryTag tag : tags) {
            set.add(tag);
        }
        return set;
    }
}
