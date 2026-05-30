# Recipe App — Requirements

## Overview

A web application that helps users discover recipes based on ingredients they already have, with dietary restriction filtering. Inspired by [Supercook](https://www.supercook.com/#/desktop).

All recipes must be exclusively Korean cuisine — only Korean recipes are included in the app.

> **Status:** All MVP features are implemented. The AI scraping agent is built but has not
> yet been run to populate data (the catalog uses 15 hand-authored seed recipes). Future
> items — ingredient substitution and recipe export/import — are not started. See the
> [Scope Summary](#scope-summary) for the per-feature breakdown.

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

### Future

- [ ] **Ingredient substitution suggestions** — show alternative ingredients when the user is missing one or two items

---

## Backend

### MVP

- [x] **Recipe database (storage)** — stores recipe data including ingredients, steps, tags, and dietary metadata. Recipes are exclusively Korean. _(JPA entities + 15 seeded recipes; H2 by default, PostgreSQL via profile.)_
- [x] **Backend business logic**
  - [x] Filter recipes by available ingredients
  - [x] Filter recipes by dietary restrictions
- [x] **AI Agent pipeline** _(implemented; runs when `ANTHROPIC_API_KEY` is set — not yet run to populate data)_
  - [x] Scrapes recipes from external sources
  - [x] Parses and structures recipe data
  - [x] Inputs results into the database
- [x] **Backend** — business logic in Java with Spring Boot (REST API, service layer, repository layer). Filters recipes by available ingredients and dietary restrictions.

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
| AI Agent     | Anthropic Claude API, called from the backend (`RecipeScraperAgent`)        |

---

## Scope Summary

| Feature                                         | Priority | Status         |
| ----------------------------------------------- | -------- | -------------- |
| Recipe list screen                              | MVP      | ✅ Done         |
| Recipe detail screen                            | MVP      | ✅ Done         |
| Ingredient-based filtering                      | MVP      | ✅ Done         |
| Dietary restriction filters (vegan, vegetarian) | MVP      | ✅ Done         |
| Recipe database + storage                       | MVP      | ✅ Done         |
| Backend filtering logic                         | MVP      | ✅ Done         |
| AI Agent for recipe scraping                    | MVP      | ✅ Built (unrun) |
| Ingredient substitution                         | Future   | ⬜ Not started  |
| Recipe export / import (text, JSON)             | Future   | ⬜ Not started  |
