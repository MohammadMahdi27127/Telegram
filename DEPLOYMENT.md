# Docker deployment quick start

## Prerequisites

- Docker Engine with the Compose plugin
- A strong PostgreSQL password

## First run

1. Copy the example environment file:

   ```bash
   cp .env.example .env
   ```

2. Edit `.env` and replace `POSTGRES_PASSWORD` with a long random value.
3. Validate the Compose configuration:

   ```bash
   docker compose config
   ```

4. Build and start the services:

   ```bash
   docker compose up --build -d
   ```

5. Check service health:

   ```bash
   docker compose ps
   docker compose logs --tail=100 db backend landing nginx
   ```

The backend container receives its PostgreSQL URL from Compose and waits for the database
health check before starting. Nginx waits for the backend and landing health checks.

## Routes

- Landing: `http://localhost/`
- Backend health through Nginx: `http://localhost/api/health`
- WebSocket: `ws://localhost/ws/chat`

## Android Debug build

Use JDK 17 and an Android SDK installation that includes API 36:

```bash
./gradlew --version
./gradlew :app:assembleDebug
```

On Windows, use `gradlew.bat :app:assembleDebug`.

## Important notes

- Do not commit `.env`, signing keys, or `local.properties`.
- The current backend still initializes tables with SQLAlchemy `create_all()` during startup.
  Replacing this with versioned Alembic migrations is intentionally deferred to the database
  engineering stage; do not assume this startup behavior is a production migration strategy.
- The Landing project currently has no committed `package-lock.json`. The Docker build creates
  a temporary lockfile in the builder before running `npm ci`. Commit a reviewed lockfile in
  the repository to make dependency resolution reproducible across builds.
- The APK under `landing/public/downloads` remains tracked because it is currently a public
  download asset. Moving it to release hosting requires a separate URL/asset migration.
