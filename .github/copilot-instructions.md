# Copilot Instructions for Scala Skeleton

## Tech Stack

- **Language:** Scala 3 (3.3.x)
- **Backend:** ZIO 2.x + ZIO HTTP
- **Frontend:** Laminar (Scala.js) + Vite
- **Desktop/Mobile:** Tauri 2.x
- **Infrastructure:** AWS CDK (Scala)
- **Build:** SBT with cross-compilation

## Coding Standards

### Scala 3 Conventions
- Use Scala 3 syntax: `given`/`using`, `enum`, indentation-based, `extension` methods
- Prefer `val` over `var`, immutable data structures
- Use `case class` for data, `enum` for ADTs
- Pattern matching over if/else chains
- No `return` statements
- Use meaningful names - no single-letter variables except in short lambdas

### ZIO Patterns (Backend)
- Use ZIO effects for all side-effectful operations
- Compose with `for` comprehensions and `ZIO.serviceWithZIO`
- Layer-based dependency injection via `ZLayer`
- Error handling with typed errors (custom error ADTs)
- Use `ZIO.attempt` for wrapping unsafe code
- Prefer `ZIO.fromOption`/`ZIO.fromEither` over manual conversion

### Laminar Patterns (Frontend)
- Reactive streams with `Var`, `Signal`, `EventBus`
- Compose UI with `div`, `span`, etc. - no JSX
- Use `child <--` and `children <--` for dynamic content
- Split large components into separate objects
- Use `Observer` for side effects in event handlers

### Project Structure
- `shared/` - Cross-compiled code (models, validation, utils)
- `frontend/` - Scala.js + Laminar + Tauri
- `backend/` - ZIO HTTP server
- `infrastructure/` - AWS CDK stacks

### General
- Keep functions small and focused
- Document public APIs with ScalaDoc
- Write tests for business logic
- Use `scalafmt` for formatting (format on compile is enabled)
- Prefer composition over inheritance
