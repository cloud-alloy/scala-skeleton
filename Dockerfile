# Stage 1: Build backend + frontend
FROM eclipse-temurin:17-jdk AS builder

# Install Node.js for Vite build
RUN apt-get update && apt-get install -y curl && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get install -y nodejs && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy build config first for better layer caching
COPY build.sbt .
COPY project/ project/

# Pre-fetch SBT and dependencies
RUN sbt update

# Copy source code
COPY shared/ shared/
COPY backend/ backend/
COPY frontend/ frontend/

# Build backend fat JAR
RUN sbt backend/assembly

# Build frontend (Scala.js -> Vite)
RUN sbt frontend/fullLinkJS
RUN cd frontend && npm install && npx vite build

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy backend JAR
COPY --from=builder /app/backend/target/scala-3.3/backend.jar ./backend.jar

# Copy frontend static assets
COPY --from=builder /app/frontend/dist ./public

EXPOSE 8080

ENV PORT=8080

CMD ["java", "-jar", "backend.jar"]
