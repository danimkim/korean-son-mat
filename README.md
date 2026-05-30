# korean-son-mat — Korean Recipe Discovery App

A web application that helps users discover recipes based on ingredients they already have, with dietary restriction filtering. All recipes are exclusively Korean cuisine.

**Live Reference:** [Supercook](https://www.supercook.com/#/desktop)

---

## Architecture Overview

### Tech Stack

| Layer      | Technology                                        |
| ---------- | ------------------------------------------------- |
| Frontend   | Next.js + TypeScript                              |
| Backend    | Java with Spring Boot (REST APIs)                 |
| Database   | PostgreSQL                                        |
| Build Tool | Maven                                             |
| Container  | Docker                                            |
| CI / CD    | GitHub Actions                                    |
| AI Agent   | Anthropic Claude API (recipe scraping)            |

### Project Structure

```
korean-son-mat/
├── frontend/                 # Next.js + TypeScript app
│   ├── pages/               # Next.js pages (recipe list, detail)
│   ├── components/          # React components
│   ├── styles/              # TailwindCSS or styled-components
│   ├── lib/                 # API client utilities
│   ├── package.json
│   └── tsconfig.json
├── backend/                 # Spring Boot application
│   ├── src/main/java/
│   │   └── com/koreansonmat/
│   │       ├── controller/  # REST controllers
│   │       ├── service/     # Business logic
│   │       ├── repository/  # Data access layer
│   │       ├── model/       # Entity classes
│   │       ├── agent/       # AI agent for recipe scraping
│   │       └── App.java     # Main application class
│   ├── src/main/resources/
│   │   └── application.properties
│   └── pom.xml
├── docker-compose.yml       # Local dev environment
├── .github/workflows/       # CI/CD pipelines
├── requirement.md
└── README.md
```

---

## Features (MVP)

### Frontend
- **Recipe List Screen** — displays recipes filtered by selected ingredients
- **Recipe Detail Screen** — full recipe view with ingredients, steps, and dietary tags
- **Ingredient Filtering** — users input available ingredients
- **Dietary Filters** — vegan, vegetarian options
- **Design** — based on Claude design system ([getdesign.md/claude](https://getdesign.md/claude/design-md))

### Backend
- **Recipe Filtering API** — filter by ingredients and dietary restrictions
- **Recipe Database** — stores Korean recipes with metadata
- **AI Agent Pipeline** — scrapes, parses, and structures recipe data from external sources
- **REST Endpoints** — CRUD operations for recipes and search

---

## Getting Started

### Prerequisites

- **Docker & Docker Compose** (for local dev environment)
- **Node.js 18+** (for frontend development)
- **Java 17+** & **Maven** (for backend development)
- **PostgreSQL** (optional, included in docker-compose)

### Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/korean-son-mat.git
   cd korean-son-mat
   ```

2. **Start services**
   ```bash
   docker-compose up -d
   ```
   This starts:
   - PostgreSQL database on `localhost:5432`
   - Backend Spring Boot API on `http://localhost:8080`
   - Frontend Next.js app on `http://localhost:3000`

3. **Initialize database**
   ```bash
   # Run migrations and seed initial data
   docker exec korean-son-mat-backend java -jar app.jar --schema.init=true
   ```

4. **Access the app**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080/api
   - Database: `localhost:5432` (user: `korean`, password: `password`)

---

## Development Setup (Local)

### Backend Setup

1. **Install dependencies**
   ```bash
   cd backend
   mvn clean install
   ```

2. **Configure database** — edit `src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/korean_recipes
   spring.datasource.username=korean
   spring.datasource.password=password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. **Run Spring Boot**
   ```bash
   mvn spring-boot:run
   ```
   Backend runs on `http://localhost:8080`

### Frontend Setup

1. **Install dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Configure API endpoint** — create `.env.local`
   ```env
   NEXT_PUBLIC_API_URL=http://localhost:8080/api
   ```

3. **Start dev server**
   ```bash
   npm run dev
   ```
   Frontend runs on `http://localhost:3000`

---

## API Endpoints (Backend)

### Recipe Endpoints

| Method | Endpoint                          | Description                           |
| ------ | --------------------------------- | ------------------------------------- |
| GET    | `/api/recipes`                    | List all recipes (with filters)       |
| GET    | `/api/recipes/{id}`               | Get recipe details                    |
| GET    | `/api/recipes/search`             | Search by ingredients & dietary tags  |
| POST   | `/api/recipes`                    | Create recipe (admin)                 |
| PUT    | `/api/recipes/{id}`               | Update recipe (admin)                 |
| DELETE | `/api/recipes/{id}`               | Delete recipe (admin)                 |

### Filter Query Parameters

```bash
# By ingredients (comma-separated)
GET /api/recipes/search?ingredients=garlic,soy-sauce,sesame-oil

# By dietary restrictions
GET /api/recipes/search?dietary=vegan,vegetarian

# Combined
GET /api/recipes/search?ingredients=rice,beef&dietary=vegetarian
```

---

## Environment Variables

### Backend (`backend/src/main/resources/application.properties`)
```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/korean_recipes
SPRING_DATASOURCE_USERNAME=korean
SPRING_DATASOURCE_PASSWORD=password
ANTHROPIC_API_KEY=your-key-here
```

### Frontend (`.env.local`)
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

---

## Running Tests

### Backend Tests
```bash
cd backend
mvn test
```

### Frontend Tests
```bash
cd frontend
npm test
```

---

## Deployment

### Build Docker Image
```bash
docker build -t korean-son-mat:latest .
```

### Deploy to Container Registry
```bash
docker tag korean-son-mat:latest your-registry/korean-son-mat:latest
docker push your-registry/korean-son-mat:latest
```

### CI/CD (GitHub Actions)
- Workflows configured in `.github/workflows/`
- Automatic build, test, and deploy on push to `main`

---

## Troubleshooting

### Port Already in Use
```bash
# Find process on port 3000
lsof -i :3000
kill -9 <PID>

# Or use different port
NEXT_PUBLIC_API_URL=http://localhost:8080/api PORT=3001 npm run dev
```

### Database Connection Error
```bash
# Verify PostgreSQL is running
docker-compose ps

# Check logs
docker-compose logs postgres
```

### Backend API Not Responding
```bash
# Check logs
docker-compose logs backend

# Rebuild
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

---

## Next Steps

- [ ] Set up frontend Next.js project
- [ ] Set up backend Spring Boot project
- [ ] Configure PostgreSQL database
- [ ] Implement recipe filtering API
- [ ] Build recipe list and detail UI
- [ ] Integrate AI agent for recipe scraping
- [ ] Write tests
- [ ] Set up GitHub Actions CI/CD

---

## References

- [Supercook](https://www.supercook.com/#/desktop) — UI inspiration
- [Claude Design System](https://getdesign.md/claude/design-md) — frontend design
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Next.js Docs](https://nextjs.org/docs)
- [PostgreSQL Docs](https://www.postgresql.org/docs)
