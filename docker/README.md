# Docker Deployment

## Quick Start

```bash
cd docker
docker-compose up -d
```

## Services

- **Frontend**: http://localhost:80
- **Backend API**: http://localhost:8080/api/
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

## Architecture

```
Frontend (Nginx:80) → Backend (Spring Boot:8081) → MySQL (3306) + Redis (6379)
```

## Health Checks

All services include health checks and will start in dependency order:
1. MySQL & Redis start first
2. Backend waits for MySQL & Redis to be healthy
3. Frontend waits for Backend to be healthy

## Stop Services

```bash
docker-compose down
```

## Rebuild

```bash
docker-compose up --build -d
```
