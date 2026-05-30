export type DietaryTag =
  | "VEGAN"
  | "VEGETARIAN"
  | "GLUTEN_FREE"
  | "DAIRY_FREE"
  | "PESCATARIAN";

export interface RecipeSummary {
  id: number;
  name: string;
  koreanName: string;
  description: string;
  thumbnailUrl: string | null;
  cookTimeMinutes: number | null;
  difficulty: string | null;
  dietaryTags: DietaryTag[];
  totalIngredients: number;
  matchedIngredients: number;
  matchedIngredientNames: string[];
  missingIngredientNames: string[];
}

export interface IngredientView {
  name: string;
  quantity: string;
  available: boolean;
}

export interface RecipeDetail {
  id: number;
  name: string;
  koreanName: string;
  description: string;
  thumbnailUrl: string | null;
  difficulty: string | null;
  cookTimeMinutes: number | null;
  servings: number | null;
  dietaryTags: DietaryTag[];
  ingredients: IngredientView[];
  steps: string[];
  matchedIngredients: number;
}

export const DIETARY_LABELS: Record<DietaryTag, string> = {
  VEGAN: "Vegan",
  VEGETARIAN: "Vegetarian",
  GLUTEN_FREE: "Gluten-free",
  DAIRY_FREE: "Dairy-free",
  PESCATARIAN: "Pescatarian",
};
