import { useCallback, useEffect, useMemo, useRef, useState } from "react";
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

const DIFFICULTY_OPTIONS = ["Easy", "Medium", "Hard"];

const COOK_TIME_OPTIONS: { label: string; value: number | null }[] = [
  { label: "Any", value: null },
  { label: "≤ 20 min", value: 20 },
  { label: "≤ 30 min", value: 30 },
  { label: "≤ 45 min", value: 45 },
  { label: "≤ 60 min", value: 60 },
];

export default function Home() {
  const [pantry, setPantry] = useState<string[]>([]);
  const [draft, setDraft] = useState("");
  const [dietary, setDietary] = useState<DietaryTag[]>([]);
  const [difficulty, setDifficulty] = useState<string[]>([]);
  const [maxCookTime, setMaxCookTime] = useState<number | null>(null);
  const [recipes, setRecipes] = useState<RecipeSummary[]>([]);
  const [suggestions, setSuggestions] = useState<string[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  // Gate persistence until after the initial restore, so the empty first-render
  // state can't clobber previously saved values (restore-vs-persist race).
  const [loaded, setLoaded] = useState(false);

  const pantryLower = useMemo(
    () => new Set(pantry.map((p) => p.toLowerCase())),
    [pantry]
  );

  // Restore the pantry and dietary selection on first load so they persist
  // across navigation/refresh. Marking `loaded` last unblocks the persist
  // effects below only after restoration has happened.
  useEffect(() => {
    try {
      const savedPantry = localStorage.getItem("son-mat:pantry");
      if (savedPantry) setPantry(JSON.parse(savedPantry));
      const savedDietary = localStorage.getItem("son-mat:dietary");
      if (savedDietary) setDietary(JSON.parse(savedDietary));
    } catch {
      /* ignore malformed storage */
    }
    setLoaded(true);
  }, []);

  useEffect(() => {
    if (!loaded) return;
    try {
      localStorage.setItem("son-mat:pantry", JSON.stringify(pantry));
    } catch {
      /* ignore */
    }
  }, [pantry, loaded]);

  // Persist the dietary selection too, so the detail page can highlight the
  // matching "make it vegan/…" substitution.
  useEffect(() => {
    if (!loaded) return;
    try {
      localStorage.setItem("son-mat:dietary", JSON.stringify(dietary));
    } catch {
      /* ignore */
    }
  }, [dietary, loaded]);

  useEffect(() => {
    getAllIngredients()
      .then(setSuggestions)
      .catch(() => setSuggestions([]));
  }, []);

  // Guards against out-of-order responses: only the most recent search may
  // update state, so a slow earlier request can't overwrite a newer result.
  const requestId = useRef(0);

  const runSearch = useCallback(async () => {
    const myRequest = ++requestId.current;
    setLoading(true);
    setError(null);
    try {
      const data = await searchRecipes(pantry, dietary, { difficulty, maxCookTime });
      if (myRequest === requestId.current) setRecipes(data);
    } catch (e) {
      if (myRequest === requestId.current) {
        setError(
          "Couldn't reach the recipe service. Make sure the backend is running on http://localhost:8080."
        );
        setRecipes([]);
      }
    } finally {
      if (myRequest === requestId.current) setLoading(false);
    }
  }, [pantry, dietary, difficulty, maxCookTime]);

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

  const toggleDifficulty = (level: string) =>
    setDifficulty((d) =>
      d.includes(level) ? d.filter((l) => l !== level) : [...d, level]
    );

  const clearAll = () => {
    setPantry([]);
    setDietary([]);
    setDifficulty([]);
    setMaxCookTime(null);
    setDraft("");
  };

  const hasActiveFilters =
    pantry.length > 0 ||
    dietary.length > 0 ||
    difficulty.length > 0 ||
    maxCookTime != null;

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

        <hr className="divider" />

        <div className="panel__label">Difficulty</div>
        <div className="toggles">
          {DIFFICULTY_OPTIONS.map((level) => (
            <button
              key={level}
              type="button"
              className={`toggle${difficulty.includes(level) ? " toggle--on" : ""}`}
              onClick={() => toggleDifficulty(level)}
            >
              {level}
            </button>
          ))}
        </div>

        <hr className="divider" />

        <div className="panel__label">Max cook time</div>
        <div className="toggles">
          {COOK_TIME_OPTIONS.map((opt) => (
            <button
              key={opt.label}
              type="button"
              className={`toggle${maxCookTime === opt.value ? " toggle--on" : ""}`}
              onClick={() => setMaxCookTime(opt.value)}
            >
              {opt.label}
            </button>
          ))}
        </div>

        {hasActiveFilters && (
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
