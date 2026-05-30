# Recipe App — Requirements

## Overview

A web application that helps users discover recipes based on ingredients they already have, with dietary restriction filtering. Inspired by [Supercook](https://www.supercook.com/#/desktop).

All recipes must be exclusively Korean cuisine — only Korean recipes are included in the app.

---

## UI

### Reference

- [Supercook](https://www.supercook.com/#/desktop) — ingredient-based recipe filtering

### Screens

#### 1. Recipe List

- Displays recipes filtered by selected ingredients
- Supports filtering by dietary restrictions (see MVP below)
- Shows recipe card with title, thumbnail, and matched ingredients at a glance

#### 2. Recipe Detail

- Full recipe view: ingredients, steps, dietary tags
- Indicates which of the user's available ingredients are used vs. missing

---

## Frontend Features

### MVP (Minimum Viable Product)

- Design should be based on the "Claude" template using: https://getdesign.md/claude/design-md
- **Ingredient-based filtering** — user inputs available ingredients and gets matching recipes
- **Dietary restriction filters**
  - Vegan
  - Vegetarian
  - _(Additional options TBD: gluten-free, dairy-free, etc.)_

### Future

- **Ingredient substitution suggestions** — show alternative ingredients when the user is missing one or two items

---

## Backend

### MVP

- **Recipe database (storage)** — stores recipe data including ingredients, steps, tags, and dietary metadata. Recipes must be exclusively Korean.
- **Backend business logic**
  - Filter recipes by available ingredients
  - Filter recipes by dietary restrictions
- **AI Agent pipeline**
  - Scrapes recipes from external sources
  - Parses and structures recipe data
  - Inputs results into the database
- **Backend** — implement business logic using Java with Spring Boot (REST API, service layer, repository layer). Filter recipes by available ingredients and dietary restrictions.

### Future

- **Recipe export / import**
  - Export recipes as text or JSON files
  - Import recipes from text or JSON files

---

## Tech Considerations _(TBD)_

| Layer        | Options / Recommendation                                               |
| ------------ | ---------------------------------------------------------------------- |
| Frontend     | Next.js, TypeScript                                                      |
| Backend      | Java with Spring Boot (REST APIs), Spring Web, Spring Data JPA          |
| Build Tool   | Maven or Gradle (pick one; provide `pom.xml` or `build.gradle`)         |
| Database     | PostgreSQL (use with Spring Data JPA; hosted options: Supabase, RDS)    |
| Container    | Docker for local dev and deployment                                     |
| CI / CD      | GitHub Actions (build, test, container image)                           |
| AI Agent     | Anthropic / OpenAI or custom agent; call from backend or separate worker |

---

## Scope Summary

| Feature                                         | Priority |
| ----------------------------------------------- | -------- |
| Recipe list screen                              | MVP      |
| Recipe detail screen                            | MVP      |
| Ingredient-based filtering                      | MVP      |
| Dietary restriction filters (vegan, vegetarian) | MVP      |
| Recipe database + storage                       | MVP      |
| Backend filtering logic                         | MVP      |
| AI Agent for recipe scraping                    | MVP      |
| Ingredient substitution                         | Future   |
| Recipe export / import (text, JSON)             | Future   |
