import { useRouter } from "next/router";
import Link from "next/link";
import { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import { getRecipe } from "../../lib/api";
import type { RecipeDetail } from "../../lib/types";
import { DIETARY_LABELS } from "../../lib/types";
import { thumbEmoji, thumbGradient } from "../../lib/thumb";

export default function RecipeDetailPage() {
  const router = useRouter();
  const { id } = router.query;
  const [recipe, setRecipe] = useState<RecipeDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id || Array.isArray(id)) return;

    let pantry: string[] = [];
    try {
      pantry = JSON.parse(localStorage.getItem("son-mat:pantry") ?? "[]");
    } catch {
      pantry = [];
    }

    setLoading(true);
    getRecipe(Number(id), pantry)
      .then((r) => {
        setRecipe(r);
        setError(null);
      })
      .catch(() => setError("This recipe could not be loaded."))
      .finally(() => setLoading(false));
  }, [id]);

  if (loading) {
    return (
      <Layout>
        <div className="state">
          <div className="state__emoji">🍲</div>
          Loading recipe…
        </div>
      </Layout>
    );
  }

  if (error || !recipe) {
    return (
      <Layout>
        <Link href="/" className="back-link">
          ← Back to recipes
        </Link>
        <div className="error-banner">{error ?? "Recipe not found."}</div>
      </Layout>
    );
  }

  const usedCount = recipe.ingredients.filter((i) => i.available).length;

  return (
    <Layout>
      <Link href="/" className="back-link">
        ← Back to recipes
      </Link>

      <div className="detail-hero">
        <div
          className="detail-thumb"
          style={{ background: thumbGradient(recipe.name) }}
        >
          {thumbEmoji(recipe.name)}
        </div>
        <div className="detail-title">
          <h1>{recipe.name}</h1>
          <div className="detail-korean">{recipe.koreanName}</div>
          <p className="detail-desc">{recipe.description}</p>

          {recipe.dietaryTags.length > 0 && (
            <div className="tags" style={{ marginTop: 14 }}>
              {recipe.dietaryTags.map((t) => (
                <span key={t} className="tag">
                  {DIETARY_LABELS[t]}
                </span>
              ))}
            </div>
          )}

          <div className="detail-meta">
            {recipe.cookTimeMinutes ? (
              <div>
                <strong>{recipe.cookTimeMinutes} min</strong>
                cook time
              </div>
            ) : null}
            {recipe.servings ? (
              <div>
                <strong>{recipe.servings}</strong>
                servings
              </div>
            ) : null}
            {recipe.difficulty ? (
              <div>
                <strong>{recipe.difficulty}</strong>
                difficulty
              </div>
            ) : null}
            <div>
              <strong>
                {usedCount}/{recipe.ingredients.length}
              </strong>
              ingredients on hand
            </div>
          </div>
        </div>
      </div>

      <div className="columns">
        <section>
          <h2 className="section-title">Ingredients</h2>
          <ul className="ingredient-list">
            {recipe.ingredients.map((ing) => (
              <li
                key={ing.name}
                className={`ingredient-row ${
                  ing.available ? "ingredient-row--have" : "ingredient-row--missing"
                }`}
              >
                <span className="ingredient-row__name">
                  <span className="ingredient-row__mark">
                    {ing.available ? "✓" : "+"}
                  </span>
                  {ing.name}
                </span>
                <span className="ingredient-row__qty">{ing.quantity}</span>
              </li>
            ))}
          </ul>
        </section>

        <section>
          <h2 className="section-title">Method</h2>
          <ol className="steps">
            {recipe.steps.map((step, i) => (
              <li key={i} className="step">
                <p>{step}</p>
              </li>
            ))}
          </ol>
        </section>
      </div>
    </Layout>
  );
}
