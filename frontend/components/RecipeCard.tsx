import Link from "next/link";
import type { RecipeSummary } from "../lib/types";
import { DIETARY_LABELS } from "../lib/types";
import { thumbEmoji, thumbGradient } from "../lib/thumb";

export default function RecipeCard({
  recipe,
  hasFilter,
}: {
  recipe: RecipeSummary;
  hasFilter: boolean;
}) {
  const matchPct =
    recipe.totalIngredients > 0
      ? Math.round((recipe.matchedIngredients / recipe.totalIngredients) * 100)
      : 0;

  return (
    <Link href={`/recipes/${recipe.id}`} className="card">
      <div
        className="card__thumb"
        style={{ background: thumbGradient(recipe.name) }}
      >
        <span>{thumbEmoji(recipe.name)}</span>
        <span className="card__korean">{recipe.koreanName}</span>
      </div>
      <div className="card__body">
        <h3 className="card__title">{recipe.name}</h3>
        <p className="card__desc">{recipe.description}</p>

        {hasFilter && (
          <div className="match-bar">
            <div className="match-bar__track">
              <div className="match-bar__fill" style={{ width: `${matchPct}%` }} />
            </div>
            <span>
              {recipe.matchedIngredients}/{recipe.totalIngredients}
            </span>
          </div>
        )}

        {recipe.dietaryTags.length > 0 && (
          <div className="tags">
            {recipe.dietaryTags.map((t) => (
              <span key={t} className="tag">
                {DIETARY_LABELS[t]}
              </span>
            ))}
          </div>
        )}

        <div className="card__meta">
          {recipe.cookTimeMinutes ? <span>⏱ {recipe.cookTimeMinutes} min</span> : null}
          {recipe.difficulty ? <span>📊 {recipe.difficulty}</span> : null}
        </div>
      </div>
    </Link>
  );
}
