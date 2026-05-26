# Pizza Delivery System

A containerized microservices pizza ordering system built with Java 21, Kotlin, Spring Boot, Docker Compose, PostgreSQL, MongoDB, Apache Kafka, and a lightweight JavaScript frontend.

The application demonstrates a small event-driven workflow: pizza tasks are created and managed through a Task Service, task status changes are published to Kafka, and a Notification Service consumes those events and stores notification history.

## Features

- Built a containerized microservices pizza ordering system using Java 21, Kotlin, Spring Boot, and Docker Compose.
- Integrated PostgreSQL, MongoDB, Apache Kafka, and a lightweight JavaScript frontend to support task management and notifications.
- Developed a Task Service with REST APIs for creating, reading, updating, and deleting pizza tasks, backed by PostgreSQL.
- Implemented asynchronous event publishing with Apache Kafka to broadcast task status changes such as `PREPARING`, `READY`, and `DELETED`.
- Built a Notification Service in Kotlin that consumes Kafka events and stores notification history in MongoDB.
- Designed a same-origin frontend deployment using Nginx reverse proxying to route `/api/tasks` and `/api/notifications` to backend services.
- Deployed the full stack on a Hetzner VPS using Docker Compose, Caddy, Cloudflare DNS, and HTTPS with Let's Encrypt.
- Configured production-ready container networking so only the frontend/reverse proxy is publicly exposed while backend services and databases remain internal.

## Tech Stack

| Area | Technologies |
| --- | --- |
| Backend | Java 21, Kotlin, Spring Boot |
| Messaging | Apache Kafka |
| Databases | PostgreSQL, MongoDB |
| Frontend | HTML, CSS, JavaScript |
| Infrastructure | Docker Compose, Nginx, Caddy, Cloudflare, Hetzner VPS |

## Services

| Service | Local Port | Description |
| --- | --- | --- |
| Frontend | `5173` | Kitchen console UI |
| Task Service | `8080` | Manages pizza tasks |
| Notification Service | `8081` | Stores and lists notification history |
| PostgreSQL | `5432` | Stores task data |
| MongoDB | `27017` | Stores notification logs |
| Kafka | `9092` | Carries pizza task events |

## API Overview

Task Service:

```http
POST   /api/tasks
GET    /api/tasks
GET    /api/tasks/{id}
PATCH  /api/tasks/{id}
DELETE /api/tasks/{id}
```

Notification Service:

```http
GET /api/notifications
```

## Running Locally

From the project folder:

```powershell
cd pizza-system
docker compose up -d --build
```

Open the frontend:

```text
http://localhost:5173
```

The frontend proxies API calls through the same origin:

```text
http://localhost:5173/api/tasks
http://localhost:5173/api/notifications
```

## Frontend Development

The frontend is dependency-free HTML, CSS, and JavaScript.

If Node.js is installed, run the local static server:

```powershell
cd pizza-system/frontend
npm run dev
```

Then open:

```text
http://localhost:5173
```

If Node.js is not installed, use Docker:

```powershell
cd pizza-system
docker compose up -d frontend
```

## Useful Commands

Rebuild one service:

```powershell
docker compose build --no-cache task-service
docker compose up -d task-service
```

View logs:

```powershell
docker logs pizza-task-service --tail 100
docker logs pizza-notification-service --tail 100
docker logs pizza-frontend --tail 100
```

Stop everything:

```powershell
docker compose down
```

Stop and remove database volumes:

```powershell
docker compose down -v
```

## Deployment

The production deployment runs on a Hetzner VPS with Docker Compose, Caddy HTTPS, Cloudflare DNS, and internal Docker networking for backend services and databases.

For the full deployment notes, see [docs/deployment.md](docs/deployment.md).