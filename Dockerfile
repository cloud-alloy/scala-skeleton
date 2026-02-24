# Stage 1: Build backend + frontend
FROM eclipse-temurin:17-jdk AS builder

# Install SBT and Node.js
RUN apt-get update && apt-get install -y curl gnupg && \
    echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | tee /etc/apt/sources.list.d/sbt.list && \
    curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x2EE0EA64E40A89B84B2DF73499E82A75642AC823" | apt-key add && \
    curl -fsSL https://deb.nodesource.com/setup_20.x | bash - && \
    apt-get update && apt-get install -y sbt nodejs && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy build config first for better layer caching
COPY build.sbt .
COPY .scalafmt.conf .
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

# Copy JAR to known location (avoids fragile Scala version path)
RUN cp $(find /app/backend/target -name "backend.jar" -type f | head -1) /app/backend-release.jar

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy backend JAR
COPY --from=builder /app/backend-release.jar ./backend.jar

# Copy frontend static assets
COPY --from=builder /app/frontend/dist ./public

EXPOSE 8080

ENV PORT=8080

CMD ["java", "-jar", "backend.jar"]
