# Scala Skeleton

Full-stack Scala 3 project skeleton with ZIO backend, Laminar frontend, Tauri desktop/mobile, and AWS CDK infrastructure.

## Tech Stack

- **Scala 3.3.x** with cross-compilation (JVM + JS)
- **Backend:** ZIO 2.x + ZIO HTTP - effect-based, type-safe HTTP server
- **Frontend:** Laminar 17.x via Scala.js - reactive UI framework
- **Desktop/Mobile:** Tauri 2.x - native wrapper
- **Infrastructure:** AWS CDK in Scala - Lambda + API Gateway + S3 + CloudFront
- **Build:** SBT multi-project with sbt-assembly, sbt-scalajs, sbt-buildinfo

## Project Structure

```
scala-skeleton/
├── shared/          # Cross-compiled (JVM + JS) - models, validation, utils
├── frontend/        # Scala.js + Laminar + Vite + Tauri
├── backend/         # ZIO HTTP server (packages to Lambda-ready JAR)
├── infrastructure/  # AWS CDK stacks (BackendStack, FrontendStack)
├── project/         # SBT plugins and build config
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
cdk synth                # Synthesise CloudFormation templates
cdk deploy --all         # Deploy all stacks to AWS
```

## Development Workflow

1. Run `sbt ~backend/run` for backend hot-reload (port 8080)
2. Run `cd frontend && npm run dev` for frontend hot-reload (port 5173)
3. Vite proxies `/api` requests to the backend automatically

## Code Conventions

- **Scala 3 syntax:** `given`/`using`, indentation-based, `enum`, `extension`
- **Immutable first:** `val` over `var`, persistent collections
- **ZIO effects:** All side effects wrapped in ZIO, typed errors, `ZLayer` DI
- **Laminar reactive:** `Var`/`Signal`/`EventBus` for state, compose with combinators
- **Formatting:** scalafmt runs on compile, do not disable
- **Testing:** ZIO Test for backend, minimal frontend tests via Scala.js

## Infrastructure

- **BackendStack:** Lambda (Java 17, 1024MB) + API Gateway with CORS
- **FrontendStack:** S3 static hosting + CloudFront CDN
- CDK uses `BuildInfo` plugin to locate the backend JAR at synth time

## CI/CD

- **PR:** Compile → Test → Assembly → Scala.js build → Vite build → Format check
- **Main merge:** Full build → CDK deploy (requires AWS OIDC credentials)
- **Auto-fix:** Issues labelled `auto-fix` trigger automated PR generation
