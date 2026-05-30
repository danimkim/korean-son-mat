import type { DietaryTag, RecipeDetail, RecipeSummary } from "./types";

const API_BASE =
  process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080/api";

function buildQuery(ingredients: string[], dietary: DietaryTag[]): string {
  const params = new URLSearchParams();
  if (ingredients.length > 0) params.set("ingredients", ingredients.join(","));
  if (dietary.length > 0) params.set("dietary", dietary.join(","));
  const qs = params.toString();
  return qs ? `?${qs}` : "";
}

export async function searchRecipes(
  ingredients: string[],
  dietary: DietaryTag[]
): Promise<RecipeSummary[]> {
  const res = await fetch(
    `${API_BASE}/recipes/search${buildQuery(ingredients, dietary)}`
  );
  if (!res.ok) throw new Error(`Failed to load recipes (${res.status})`);
  return res.json();
}

export async function getRecipe(
  id: number,
  ingredients: string[]
): Promise<RecipeDetail> {
  const qs = ingredients.length > 0 ? `?ingredients=${ingredients.join(",")}` : "";
  const res = await fetch(`${API_BASE}/recipes/${id}${qs}`);
  if (!res.ok) throw new Error(`Recipe not found (${res.status})`);
  return res.json();
}

export async function getAllIngredients(): Promise<string[]> {
  const res = await fetch(`${API_BASE}/recipes/ingredients`);
  if (!res.ok) throw new Error(`Failed to load ingredients (${res.status})`);
  return res.json();
}
