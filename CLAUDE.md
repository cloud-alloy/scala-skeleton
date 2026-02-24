# Scala Skeleton

Full-stack Scala 3 project skeleton with ZIO backend, Laminar frontend, and Tauri desktop/mobile support. Deploys to Fly.io.

## Tech Stack

- **Scala 3.3.x** with cross-compilation (JVM + JS)
- **Backend:** ZIO 2.x + ZIO HTTP - effect-based, type-safe HTTP server
- **Frontend:** Laminar 17.x via Scala.js - reactive UI framework
- **Desktop/Mobile:** Tauri 2.x - native wrapper
- **Deployment:** Fly.io (Docker) - single machine serves API + static frontend
- **Build:** SBT multi-project with sbt-assembly, sbt-scalajs

## Project Structure

```
scala-skeleton/
├── shared/          # Cross-compiled (JVM + JS) - models, validation, utils
├── frontend/        # Scala.js + Laminar + Vite + Tauri
├── backend/         # ZIO HTTP server (serves API + static files in prod)
├── project/         # SBT plugins and build config
├── Dockerfile       # Multi-stage build (JDK builder → JRE runtime)
├── fly.toml         # Fly.io deployment config
└── build.sbt        # Multi-project build definition
```

## Build Commands

```bash
sbt compile              # Compile all projects
sbt test                 # Run all tests
sbt backend/assembly     # Build backend fat JAR
sbt frontend/fullLinkJS  # Compile Scala.js (production)
sbt frontend/fastLinkJS  # Compile Scala.js (development)
cd frontend && npm run dev    # Start Vite dev server (port 5173)
cd frontend && npx vite build # Build frontend for deployment
```

## Development Workflow

1. Run `sbt ~backend/run` for backend hot-reload (port 8080)
2. Run `cd frontend && npm run dev` for frontend hot-reload (port 5173)
3. Vite proxies `/api` requests to the backend automatically

## Deployment

Deploys to Fly.io via Docker. The backend serves both:
- `/api/*` and `/health` — ZIO HTTP API routes
- `/*` — Static frontend assets with SPA fallback

```bash
fly deploy           # Deploy to Fly.io
fly logs             # Tail production logs
fly status           # Check deployment status
```

The Dockerfile is a multi-stage build:
1. Builder stage: JDK 17 + Node 20, builds backend JAR + Vite frontend
2. Runtime stage: JRE 17 only, runs the fat JAR with frontend in `/public`

## Code Conventions

- **Scala 3 syntax:** `given`/`using`, indentation-based, `enum`, `extension`
- **Immutable first:** `val` over `var`, persistent collections
- **ZIO effects:** All side effects wrapped in ZIO, typed errors, `ZLayer` DI
- **Laminar reactive:** `Var`/`Signal`/`EventBus` for state, compose with combinators
- **Formatting:** scalafmt runs on compile, do not disable
- **Testing:** ZIO Test for backend, minimal frontend tests via Scala.js

## CI/CD

- **PR:** Compile → Test → Assembly → Scala.js build → Vite build → Format check
- **Main merge:** Deploy to Fly.io via `flyctl deploy --remote-only`
- **Auto-fix:** Issues labelled `auto-fix` trigger automated PR generation

## Environment

- `PORT` env var controls server port (default 8080, set by Fly.io)
- Backend auto-detects `/public` directory for static file serving
