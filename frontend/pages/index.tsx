import { useCallback, useEffect, useMemo, useState } from "react";
import Layout from "../components/Layout";
import RecipeCard from "../components/RecipeCard";
import { getAllIngredients, searchRecipes } from "../lib/api";
import type { DietaryTag, RecipeSummary } from "../lib/types";
import { DIETARY_LABELS } from "../lib/types";

const DIETARY_OPTIONS: DietaryTag[] = [
  "VEGAN",
  "VEGETARIAN",
  "GLUTEN_FREE",
  "DAIRY_FREE",
  "PESCATARIAN",
];

export default function Home() {
  const [pantry, setPantry] = useState<string[]>([]);
  const [draft, setDraft] = useState("");
  const [dietary, setDietary] = useState<DietaryTag[]>([]);
  const [recipes, setRecipes] = useState<RecipeSummary[]>([]);
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const pantryLower = useMemo(
    () => new Set(pantry.map((p) => p.toLowerCase())),
    [pantry]
  );

  // Restore the pantry on first load so it persists across navigation/refresh.
  useEffect(() => {
    try {
      const saved = localStorage.getItem("son-mat:pantry");
      if (saved) setPantry(JSON.parse(saved));
    } catch {
      /* ignore malformed storage */
    }
  }, []);

  useEffect(() => {
    try {
      localStorage.setItem("son-mat:pantry", JSON.stringify(pantry));
    } catch {
      /* ignore */
    }
  }, [pantry]);

  useEffect(() => {
    getAllIngredients()
      .then(setSuggestions)
      .catch(() => setSuggestions([]));
  }, []);

  const runSearch = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      setRecipes(await searchRecipes(pantry, dietary));
    } catch (e) {
      setError(
        "Couldn't reach the recipe service. Make sure the backend is running on http://localhost:8080."
      );
      setRecipes([]);
    } finally {
      setLoading(false);
    }
  }, [pantry, dietary]);

  useEffect(() => {
    runSearch();
  }, [runSearch]);

  const addIngredient = (raw: string) => {
    const name = raw.trim();
    if (!name || pantryLower.has(name.toLowerCase())) return;
    setPantry((p) => [...p, name]);
    setDraft("");
  };

  const removeIngredient = (name: string) =>
    setPantry((p) => p.filter((i) => i !== name));

  const toggleSuggestion = (name: string) => {
    if (pantryLower.has(name.toLowerCase())) removeIngredient(name);
    else setPantry((p) => [...p, name]);
  };

  const toggleDietary = (tag: DietaryTag) =>
    setDietary((d) => (d.includes(tag) ? d.filter((t) => t !== tag) : [...d, tag]));

  const clearAll = () => {
    setPantry([]);
    setDietary([]);
    setDraft("");
  };

  const hasFilter = pantry.length > 0;
  const topSuggestions = suggestions.slice(0, 16);

  return (
    <Layout>
      <section className="hero">
        <h1>What can I cook tonight?</h1>
        <p>
          Add the ingredients sitting in your fridge and pantry. We’ll surface the
          Korean dishes you can make right now — ranked by how much you already have.
        </p>
      </section>

      <div className="panel">
        <div className="panel__label">Your ingredients</div>
        <form
          className="ingredient-input"
          onSubmit={(e) => {
            e.preventDefault();
            addIngredient(draft);
          }}
        >
          <input
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            placeholder="e.g. kimchi, rice, egg, garlic…"
            aria-label="Add an ingredient"
          />
          <button type="submit" className="btn btn--primary">
            Add
          </button>
        </form>

        {pantry.length > 0 && (
          <div className="chips">
            {pantry.map((name) => (
              <span key={name} className="chip">
                {name}
                <button
                  type="button"
                  aria-label={`Remove ${name}`}
                  onClick={() => removeIngredient(name)}
                >
                  ×
                </button>
              </span>
            ))}
          </div>
        )}

        {topSuggestions.length > 0 && (
          <div className="suggestions">
            {topSuggestions.map((name) => {
              const active = pantryLower.has(name.toLowerCase());
              return (
                <button
                  key={name}
                  type="button"
                  className={`suggestion${active ? " suggestion--active" : ""}`}
                  onClick={() => toggleSuggestion(name)}
                >
                  {active ? "✓ " : "+ "}
                  {name}
                </button>
              );
            })}
          </div>
        )}

        <hr className="divider" />

        <div className="panel__label">Dietary restrictions</div>
        <div className="toggles">
          {DIETARY_OPTIONS.map((tag) => (
            <button
              key={tag}
              type="button"
              className={`toggle${dietary.includes(tag) ? " toggle--on" : ""}`}
              onClick={() => toggleDietary(tag)}
            >
              {DIETARY_LABELS[tag]}
            </button>
          ))}
        </div>

        {(pantry.length > 0 || dietary.length > 0) && (
          <>
            <hr className="divider" />
            <button type="button" className="btn btn--ghost" onClick={clearAll}>
              Clear all filters
            </button>
          </>
        )}
      </div>

      {error && <div className="error-banner">{error}</div>}

      <div className="results-head">
        <h2>{hasFilter ? "Recipes you can make" : "All Korean recipes"}</h2>
        <span className="results-count">
          {loading ? "Searching…" : `${recipes.length} recipe${recipes.length === 1 ? "" : "s"}`}
        </span>
      </div>

      {loading ? (
        <div className="state">
          <div className="state__emoji">🍳</div>
          Finding your recipes…
        </div>
      ) : recipes.length === 0 && !error ? (
        <div className="state">
          <div className="state__emoji">🔍</div>
          No recipes match those filters yet. Try removing a dietary restriction or
          adding more ingredients.
        </div>
      ) : (
        <div className="grid">
          {recipes.map((r) => (
            <RecipeCard key={r.id} recipe={r} hasFilter={hasFilter} />
          ))}
        </div>
      )}
    </Layout>
  );
}
