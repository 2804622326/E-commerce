# SportsCenter E-commerce

A full-stack e-commerce application built with Spring Boot and React, featuring complete CI/CD pipeline (Jenkins + Docker + AWS ECR/EC2), comprehensive unit/integration testing, and code coverage reporting.

## Features
- Product browsing with filtering (brands/types) and pagination
- Shopping basket and order management
- JWT-based authentication and authorization
- Swagger/OpenAPI documentation

## Tech Stack
- **Backend**: Spring Boot 3 (Java 17), Spring Security (JWT), Spring Data JPA, Redis
- **Frontend**: React + Vite + TypeScript, Vitest + Testing Library
- **Build & Quality**: Maven, Jacoco code coverage
- **DevOps**: Docker (buildx multi-platform), Jenkins Pipeline, AWS ECR/EC2, Nginx

## Project Structure
- Backend: `src/main/java/com/ecommerce/sportscenter` (controller, service, repository, entity, security, config)
- Frontend: `client/src` (features, app, assets, tests)
- Deployment: `docker/` (compose files, Dockerfiles, nginx.conf, data.sql)
- CI/CD: `Jenkinsfile`

## Local Development

### Backend (Spring Boot)
- Configuration: [src/main/resources/application.yaml](src/main/resources/application.yaml)
- Default port: `8081`
- Start locally (requires JDK 17 and Maven):
  ```bash
  ./mvnw spring-boot:run
  ```
- Swagger UI: `http://localhost:8081/swagger-ui/index.html`

### Frontend (React + Vite)
- Working directory: `client`
- Environment variable: `VITE_API_URL` (defaults to `http://localhost:8081`)
- Start locally:
  ```bash
  cd client
  npm install
  npm run dev
  ```

## Testing & Coverage

### Backend (Maven)
```bash
./mvnw test
./mvnw jacoco:report
```
Report location: `target/site/jacoco/index.html`

### Frontend (Vitest)
```bash
cd client
npm run test
npm run coverage
```

## Docker & Deployment

### Images
- Backend: `docker/Dockerfile.backend`
- Frontend (Nginx static hosting): `docker/Dockerfile.frontend`
  - Uses `ARG VITE_API_URL` to inject backend API URL during build time

### Docker Compose (EC2 Deployment)
- File: `docker/docker-compose.ec2.yml`
- Services: MySQL, Redis, Backend, Frontend (Nginx)
- Health checks:
  - MySQL/Redis: Container-level healthcheck
  - Backend: `/api/products?PageSize=1`
  - Frontend: `/` returns 200

### Environment Variables (Critical)
**Frontend:**
- `VITE_API_URL` (e.g., `http://<EC2_IP>:8081`)

**Backend (Spring Boot):**
- `SPRING_DATASOURCE_URL` (e.g., `jdbc:mysql://sportscenter-mysql:3306/sports-center?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`)
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_REDIS_HOST` / `SPRING_REDIS_PORT`

> **Note**: Docker Compose overrides default database configuration from [application.yaml](src/main/resources/application.yaml). Ensure DB name/credentials match runtime environment.

## Jenkins CI/CD Pipeline
- Pipeline file: `Jenkinsfile`
- Stages:
  1. Checkout code
  2. Build & Test (Maven for backend, Vitest for frontend)
  3. Docker buildx build (backend/frontend images)
  4. Push to AWS ECR
  5. Deploy to EC2 (via `scp` deploy script + `ssh` execution with health checks)
  6. Health check validation and deployment report

### Dynamic IP Resolution
The pipeline uses AWS CLI to dynamically resolve EC2 public IP for:
- Frontend image build (injecting `VITE_API_URL`)
- Deployment and health check stages (`ssh`/`curl` targets)

## Runtime Ports
- Backend: `8081`
- Frontend: `80`

## Authentication

### Login API
- Endpoint: `POST /api/auth/login`
- Demo credentials (in-memory user for demonstration):
  - Username: `rahul`
  - Password: `Password`
- Successful login returns JWT token
- Subsequent requests require `Authorization: Bearer <token>` header

## Troubleshooting

### Login 500 Error
- Current login uses in-memory user (see [MyConfig](src/main/java/com/ecommerce/sportscenter/config/MyConfig.java)), independent of database
- If database errors appear in startup logs, verify consistency between Compose and Spring datasource:
  - DB name: `sports-center` vs `sportscenter`
  - Username: `admin` vs `root`
  - Password: `Liminghao2001` vs `password`
- To use database-backed user authentication:
  1. Integrate Flyway with `db/migration` scripts for schema/data
  2. Set `spring.jpa.hibernate.ddl-auto` to `validate`
  3. Remove in-memory user and configure `UserDetailsService` to use database

### CORS Issues
- CORS is configured in [CorsConfig](src/main/java/com/ecommerce/sportscenter/config/CorsConfig.java) with `allowedOriginPatterns("*")` and `allowCredentials(true)`
- Frontend must call backend using the injected `VITE_API_URL`

### Frontend Container Not Running
- Deploy script waits for backend health before starting frontend
- Validates Nginx returns 200 status

### EC2 Public IP Changes
- **Option 1**: Assign Elastic IP (recommended for production)
- **Option 2**: Continue with current approach (pipeline dynamically queries IP)

## Developer Notes
- **Database initialization**: `docker/data.sql` can be used for initial data import (mind foreign key constraints)
- **Naming convention**: Entity `@Table(name=...)` uses PascalCase (e.g., `Product`, `Orders`)
- **Coverage reporting**: Maven Jacoco and Vitest coverage scripts are ready for CI integration

## Quick Verification

### Local
1. Start backend on port `8081`
2. Start frontend (`npm run dev`), access `http://localhost:5173`
3. Login with: `rahul` / `Password`

### Production (EC2)
- Frontend: `http://<EC2_IP>/`
- Backend health check: `http://<EC2_IP>:8081/api/products?PageSize=1`

---

**Project Highlights for Job Applications:**
- Full-stack development with modern frameworks (Spring Boot 3, React)
- Complete CI/CD automation with Jenkins, Docker multi-platform builds
- Cloud deployment on AWS (ECR, EC2)
- Comprehensive testing suite with code coverage reporting
- Production-ready architecture with health checks, Redis caching, and JWT security
