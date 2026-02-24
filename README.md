# Scala Full-Stack App

A full-stack Scala 3 skeleton demonstrating:
- **Scala 3** - Core language
- **Scala.js** - Compiles Scala to JavaScript
- **Laminar** - Reactive UI library
- **ZIO** - Effect system (backend & shared)
- **ZIO HTTP** - Backend server (serves API + static files)
- **Vite** - Frontend build tool
- **Tauri** - Desktop/mobile native apps
- **Fly.io** - Deployment (Docker)

## Project Structure

```
scala-skeleton/
├── build.sbt              # SBT configuration
├── project/               # SBT plugins
├── shared/                # Code shared between frontend & backend
├── frontend/              # Scala.js + Laminar UI
│   ├── src-tauri/         # Tauri config for desktop/mobile
│   └── vite.config.js     # Vite configuration
├── backend/               # ZIO HTTP server
├── Dockerfile             # Multi-stage production build
└── fly.toml               # Fly.io deployment config
```

## Prerequisites

- JDK 17+ (for Scala/SBT)
- SBT (Scala Build Tool)
- Node.js 20+ (for Vite)
- Rust (for Tauri desktop/mobile)
- flyctl (for deployment)

## Running the App

### Web Development

**Terminal 1 - Backend:**
```bash
sbt ~backend/run
```

**Terminal 2 - Frontend:**
```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

### Desktop App (Tauri)

```bash
cd frontend
npm install
npm run tauri dev
```

### Mobile App (Tauri)

**iOS:**
```bash
cd frontend
npm run tauri ios init    # First time only
npm run tauri -- ios dev "iPhone 17 Pro"
```

**Android:**
```bash
cd frontend
npm run tauri android init    # First time only
npm run tauri android dev
```

## Deployment

```bash
fly launch       # First time: creates Fly.io app
fly deploy       # Deploy (or auto-deploys on push to main)
```

## API Endpoints

- `GET /health` - Health check
- `GET /api/message` - Returns hello message
- `GET /api/greet/:name` - Returns personalised greeting

## CI/CD

- **Pull requests** trigger build + test
- **Merges to main** auto-deploy to Fly.io
- **Issues labelled `auto-fix`** trigger automated PR generation
