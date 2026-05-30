# Recipe App — Requirements

## Overview

A web application that helps users discover recipes based on ingredients they already have, with dietary restriction filtering. Inspired by [Supercook](https://www.supercook.com/#/desktop).

All recipes must be exclusively Korean cuisine — only Korean recipes are included in the app.

> **Status:** All MVP features are implemented, plus difficulty/cook-time filters and
> ingredient substitution suggestions. The catalog uses 15 hand-authored seed recipes.
> The AI scraping agent was removed (deferred — not needed yet); recipe export/import is
> not started. See the [Scope Summary](#scope-summary) for the per-feature breakdown.

---

## UI

### Reference

- [Supercook](https://www.supercook.com/#/desktop) — ingredient-based recipe filtering

### Screens

#### 1. Recipe List ✅ _Done_

- [x] Displays recipes filtered by selected ingredients
- [x] Supports filtering by dietary restrictions (see MVP below)
- [x] Shows recipe card with title, thumbnail, and matched ingredients at a glance

#### 2. Recipe Detail ✅ _Done_

- [x] Full recipe view: ingredients, steps, dietary tags
- [x] Indicates which of the user's available ingredients are used vs. missing

---

## Frontend Features

### MVP (Minimum Viable Product)

- [x] Design based on the "Claude" template ([getdesign.md/claude](https://getdesign.md/claude/design-md)) — warm cream canvas, terracotta accent, serif display
- [x] **Ingredient-based filtering** — user inputs available ingredients and gets matching recipes, ranked by match count
- [x] **Dietary restriction filters**
  - [x] Vegan
  - [x] Vegetarian
  - [x] Also implemented: gluten-free, dairy-free, pescatarian
- [x] **Difficulty & cook-time filters** _(added)_ — narrow results to Easy/Medium/Hard and a maximum cook time (≤ 20 / 30 / 45 / 60 min)

### Future

- [x] **Ingredient substitution suggestions** — alternative ingredients shown on the recipe detail (e.g. Beef sirloin → plant-based beef / tofu for vegan/vegetarian), grouped into per-diet "make it vegan/gluten-free" adaptations

---

## Backend

### MVP

- [x] **Recipe database (storage)** — stores recipe data including ingredients, steps, tags, and dietary metadata. Recipes are exclusively Korean. _(JPA entities + 15 seeded recipes; H2 by default, PostgreSQL via profile.)_
- [x] **Backend business logic**
  - [x] Filter recipes by available ingredients
  - [x] Filter recipes by dietary restrictions
  - [x] Filter recipes by difficulty and maximum cook time _(added)_
- [ ] **AI Agent pipeline** _(removed — deferred; scraping not needed yet)_
  - [ ] Scrapes recipes from external sources
  - [ ] Parses and structures recipe data
  - [ ] Inputs results into the database
- [x] **Backend** — business logic in Java with Spring Boot (REST API, service layer, repository layer). Filters recipes by available ingredients, dietary restrictions, difficulty, and cook time.

### Future

- [ ] **Recipe export / import**
  - [ ] Export recipes as text or JSON files
  - [ ] Import recipes from text or JSON files

---

## Tech Considerations (Decided and Implemented)

| Layer        | Choice                                                                      |
| ------------ | --------------------------------------------------------------------------- |
| Frontend     | Next.js 14 (Pages Router) + TypeScript                                       |
| Backend      | Java 17 + Spring Boot 3.3 (Spring Web, Spring Data JPA)                      |
| Build Tool   | Maven (`backend/pom.xml`)                                                    |
| Database     | H2 in-memory by default; PostgreSQL via the `postgres` profile (Docker)     |
| Container    | Docker + Docker Compose (per-service Dockerfiles)                            |
| CI / CD      | GitHub Actions — backend test, frontend build, Docker image builds          |
| AI Agent     | _Removed (deferred) — recipe scraping not needed yet_                        |

---

## Scope Summary

| Feature                                         | Priority | Status         |
| ----------------------------------------------- | -------- | -------------- |
| Recipe list screen                              | MVP      | ✅ Done         |
| Recipe detail screen                            | MVP      | ✅ Done         |
| Ingredient-based filtering                      | MVP      | ✅ Done         |
| Dietary restriction filters (vegan, vegetarian) | MVP      | ✅ Done         |
| Difficulty & cook-time filters                  | Added    | ✅ Done         |
| Recipe database + storage                       | MVP      | ✅ Done         |
| Backend filtering logic                         | MVP      | ✅ Done         |
| AI Agent for recipe scraping                    | MVP      | ⬜ Removed (deferred) |
| Ingredient substitution                         | Future   | ✅ Done         |
| Recipe export / import (text, JSON)             | Future   | ⬜ Not started  |
