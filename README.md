# Scala Full-Stack App

A minimal proof-of-concept demonstrating:
- **Scala 3** - Core language
- **Scala.js** - Compiles Scala to JavaScript
- **Laminar** - Reactive UI library
- **ZIO** - Effect system (backend & shared)
- **ZIO HTTP** - Backend server
- **Vite** - Frontend build tool
- **Tauri** - Desktop/mobile native apps

## Project Structure

```
scala_project/
├── build.sbt              # SBT configuration
├── project/               # SBT plugins
├── shared/                # Code shared between frontend & backend
├── frontend/              # Scala.js + Laminar UI
│   ├── src-tauri/         # Tauri config for desktop/mobile
│   └── vite.config.js     # Vite configuration
└── backend/               # ZIO HTTP server
```

## Prerequisites

- JDK 17+ (for Scala/SBT)
- SBT (Scala Build Tool)
- Node.js 18+ (for Vite)
- Rust (for Tauri desktop/mobile)

## Running the App

### Web Development

**Terminal 1 - Compile Scala.js:**
```bash
cd scala_project
sbt ~frontend/fastLinkJS
```

**Terminal 2 - Run Vite dev server:**
```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173

### Backend Server

```bash
cd scala_project
sbt backend/run
```

Server runs at http://localhost:8080

### Desktop App (Tauri)

```bash
cd frontend
npm install
npm run tauri dev
```

### Mobile App (Tauri)

**iOS:**

First, initialize the iOS target (only needed once):
```bash
cd frontend
npm run tauri ios init
```

To run on the iOS simulator:

1. Boot a simulator first:
```bash
xcrun simctl list devices available | grep iPhone  # List available devices
xcrun simctl boot "iPhone 17 Pro"                  # Boot your chosen device
open -a Simulator                                   # Open Simulator app
```

2. Run the build with the device name:
```bash
npm run tauri -- ios dev "iPhone 17 Pro"
```

Note: The device name is a positional argument, not a flag. The Rust compilation takes ~1-2 minutes on first build.

**Android:**

First, initialize the Android target (only needed once):
```bash
cd frontend
npm run tauri android init
```

To run on an Android emulator:
```bash
npm run tauri android dev
```

## API Endpoints

- `GET /health` - Health check
- `GET /api/message` - Returns hello message
- `GET /api/greet/:name` - Returns personalized greeting
