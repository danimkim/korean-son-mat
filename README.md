# korean-son-mat — Korean Recipe Discovery App

**손맛 (son-mat)** — “the taste of one’s hands.”

A full-stack web app that helps you discover **Korean** recipes from the ingredients
you already have, with dietary-restriction filtering. Inspired by
[Supercook](https://www.supercook.com/#/desktop). All recipes are exclusively Korean cuisine.

Add the ingredients in your fridge and pantry, and the app surfaces the dishes you can
make right now — ranked by how much you already have.

---

## Architecture Overview

### Tech Stack

| Layer      | Technology                                                        |
| ---------- | ----------------------------------------------------------------- |
| Frontend   | Next.js 14 (Pages Router) + TypeScript, plain-CSS design tokens   |
| Backend    | Java 17 + Spring Boot 3.3 (Spring Web, Spring Data JPA)           |
| Database   | H2 in-memory by default · PostgreSQL via the `postgres` profile   |
| Build Tool | Maven (backend) · npm (frontend)                                  |
| Container  | Docker + Docker Compose                                           |
| CI / CD    | GitHub Actions                                                    |

### Project Structure

```
korean-son-mat/
├── frontend/                      # Next.js + TypeScript app
│   ├── pages/                     # _app, index (list), recipes/[id] (detail)
│   ├── components/                # Layout, RecipeCard
│   ├── lib/                       # api client, types, thumbnail helper
│   ├── styles/globals.css         # Claude-inspired design tokens (plain CSS)
│   ├── Dockerfile
│   └── package.json
├── backend/                       # Spring Boot application
│   ├── src/main/java/com/koreansonmat/
│   │   ├── controller/            # RecipeController (REST)
│   │   ├── service/               # RecipeService (filtering logic)
│   │   ├── repository/            # RecipeRepository (Spring Data JPA)
│   │   ├── model/                 # Recipe, Ingredient, DietaryTag
│   │   ├── dto/                   # API response records
│   │   ├── config/                # WebConfig (CORS), DataSeeder
│   │   └── App.java               # Main application class
│   ├── src/main/resources/
│   │   ├── application.properties           # default profile (H2)
│   │   └── application-postgres.properties  # postgres profile
│   ├── src/test/java/             # service + controller tests
│   ├── Dockerfile
│   └── pom.xml
├── docker-compose.yml             # postgres + backend + frontend
├── .github/workflows/ci.yml       # CI: backend, frontend, docker
├── requirement.md
└── README.md
```

---

## Features

### Frontend

- **Recipe List** — ingredient picker with suggestion chips, dietary toggles, and live
  "match" bars showing how much of each recipe you already have.
- **Recipe Detail** — full ingredients (marked available vs. missing), numbered steps,
  dietary tags, cook time, and servings.
- **Ingredient filtering** — your pantry persists in `localStorage` across navigation.
- **Dietary filters** — vegan, vegetarian, gluten-free, dairy-free, pescatarian.
- **Difficulty & cook-time filters** — narrow results to Easy/Medium/Hard and a maximum
  cook time (≤ 20 / 30 / 45 / 60 min).
- **Ingredient substitutions** — the detail view suggests alternatives (e.g. beef →
  plant-based beef / tofu) and groups them into per-diet "make it vegan/gluten-free" swaps.
- **Design** — warm, editorial aesthetic inspired by the Claude design system
  ([getdesign.md/claude](https://getdesign.md/claude/design-md)).

### Backend

- **Search API** — filter by available ingredients (ranked by match count, Supercook-style),
  dietary restrictions, difficulty, and maximum cook time.
- **Ingredient substitutions** — recipe detail is enriched with substitute ingredients and
  per-diet adaptations (curated map in `IngredientSubstitutions`).
- **Recipe catalog** — seeded with **15 curated Korean recipes** on first start
  (see [Recipe data & provenance](#recipe-data--provenance)).
- **REST endpoints** — list, search, ingredient catalog, detail, create, delete.

---

## Getting Started

### Prerequisites

- **Docker & Docker Compose** — for the one-command setup, **or**
- **Node.js 18+** (frontend) and **Java 17+ with Maven** (backend) — for local dev.

PostgreSQL is **not** required locally — the backend defaults to an embedded H2 database.

### Quick Start with Docker

```bash
git clone https://github.com/danimkim/korean-son-mat.git
cd korean-son-mat
docker compose up --build
```

This starts three services and **auto-seeds** the recipe catalog (no manual init step):

- PostgreSQL on `localhost:5432` (db `korean_recipes`, user `korean`, password `password`)
- Backend API on `http://localhost:8080`
- Frontend on `http://localhost:3000`

Open **http://localhost:3000**.

---

## Development Setup (Local, no Docker)

### Backend

```bash
cd backend
mvn spring-boot:run
```

Runs on `http://localhost:8080` using an in-memory H2 database that is **auto-seeded** on
startup — no database setup needed. The H2 console is available at
`http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:korean_recipes`, user `sa`).

To run against PostgreSQL instead:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
# honors SPRING_DATASOURCE_URL / _USERNAME / _PASSWORD (see application-postgres.properties)
```

### Frontend

```bash
cd frontend
npm install
cp .env.local.example .env.local   # NEXT_PUBLIC_API_URL=http://localhost:8080/api
npm run dev
```

Runs on `http://localhost:3000`.

---

## API Endpoints

Base path: `/api/recipes`

| Method | Endpoint                     | Description                                  |
| ------ | ---------------------------- | -------------------------------------------- |
| GET    | `/api/recipes`               | List all recipes (accepts the same filters)  |
| GET    | `/api/recipes/search`        | Search by ingredients, dietary, difficulty, cook time |
| GET    | `/api/recipes/ingredients`   | Distinct ingredient names (for the picker)   |
| GET    | `/api/recipes/{id}`          | Recipe detail (optionally `?ingredients=…`)  |
| POST   | `/api/recipes`               | Create a recipe                              |
| DELETE | `/api/recipes/{id}`          | Delete a recipe                              |

### Filter query parameters

All parameters are optional. `ingredients`, `dietary`, and `difficulty` are comma-separated,
case-insensitive lists; `maxCookTime` is minutes.

| Param         | Example              | Meaning                                        |
| ------------- | -------------------- | ---------------------------------------------- |
| `ingredients` | `garlic,soy-sauce`   | Rank results by how many you have              |
| `dietary`     | `vegan,gluten-free`  | Result must satisfy ALL listed tags            |
| `difficulty`  | `easy,medium`        | Result difficulty must be one of these         |
| `maxCookTime` | `30`                 | Cook time ≤ this many minutes                  |

```bash
# By ingredients — results ranked by how many you have
GET /api/recipes/search?ingredients=garlic,soy-sauce,sesame-oil

# Quick, easy, vegan dishes
GET /api/recipes/search?dietary=vegan&difficulty=easy&maxCookTime=30

# Combined
GET /api/recipes/search?ingredients=spinach,garlic&dietary=vegan&maxCookTime=45
```

Valid dietary tags: `VEGAN`, `VEGETARIAN`, `GLUTEN_FREE`, `DAIRY_FREE`, `PESCATARIAN`.
Valid difficulties: `Easy`, `Medium`, `Hard`.

---

## Environment Variables

### Backend

| Variable                     | Default                  | Notes                                   |
| ---------------------------- | ------------------------ | --------------------------------------- |
| `SPRING_PROFILES_ACTIVE`     | _(default = H2)_         | Set to `postgres` for PostgreSQL        |
| `SPRING_DATASOURCE_URL`      | `jdbc:postgresql://…`    | Used by the `postgres` profile          |
| `SPRING_DATASOURCE_USERNAME` | `korean`                 | Used by the `postgres` profile          |
| `SPRING_DATASOURCE_PASSWORD` | `password`               | Used by the `postgres` profile          |
| `APP_CORS_ALLOWED_ORIGINS`   | `http://localhost:3000`  | Allowed browser origin(s)               |

### Frontend (`.env.local`)

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

---

## Running Tests

### Backend (10 tests: service + controller)

```bash
cd backend
mvn test
```

### Frontend

```bash
cd frontend
npm run build   # type-checks and builds
npm test        # placeholder — no unit tests configured yet
```

---

## Recipe data & provenance

The catalog is seeded by `backend/.../config/DataSeeder.java` with **15 hand-authored
Korean recipes** (Bibimbap, Japchae, Kimchi Jjigae, Bulgogi, Tteokbokki, and more). These
were written from general Korean-cuisine knowledge — not verified against an authoritative
source; dietary tags are best-effort.

Add more recipes by editing `DataSeeder.java` or via `POST /api/recipes`.

---

## Deployment

### Build images

```bash
docker build -t korean-son-mat-backend  ./backend
docker build -t korean-son-mat-frontend ./frontend
```

Each service has its own multi-stage `Dockerfile`. The backend image runs with the
`postgres` profile by default.

### CI/CD (GitHub Actions)

`.github/workflows/ci.yml` runs on push / PR to `main`:

- **backend** — `mvn clean verify` on JDK 17
- **frontend** — `npm install` + `npm run build` (type-check) on Node 20
- **docker** — builds both images after the above pass

---

## Troubleshooting

**Port already in use**

```bash
lsof -ti:8080,3000 | xargs kill        # free both ports
# or run the frontend elsewhere:
PORT=3001 npm run dev
```

**Frontend can't reach the API** — confirm the backend is up on `http://localhost:8080`
and that `NEXT_PUBLIC_API_URL` matches. CORS allows `http://localhost:3000` by default
(override with `APP_CORS_ALLOWED_ORIGINS`).

**Docker database/connection issues**

```bash
docker compose ps
docker compose logs postgres
docker compose logs backend
docker compose down && docker compose up --build
```

---

## Roadmap

Delivered (MVP): recipe list & detail screens, ingredient + dietary filtering,
difficulty & cook-time filters, ingredient substitution suggestions, recipe database
with seed data, backend filtering logic, Docker, and CI.

Next:

- [ ] Expand the seeded recipe catalog with more dishes
- [ ] `PUT /api/recipes/{id}` for admin edits
- [ ] Recipe export / import as text or JSON (Future in `requirement.md`)
- [ ] Frontend unit / component tests

---

## References

- [Supercook](https://www.supercook.com/#/desktop) — UI inspiration
- [Claude Design System](https://getdesign.md/claude/design-md) — frontend design
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Next.js Docs](https://nextjs.org/docs)
- [PostgreSQL Docs](https://www.postgresql.org/docs)
