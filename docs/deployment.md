# Deployment

This project is a full Docker app. GitHub Pages can host only the static frontend, but the full system also needs Java/Kotlin services, PostgreSQL, MongoDB, and Kafka. A VPS with Docker Compose is the simplest deployment path.

## 1. Prepare A VPS

Use a small Ubuntu VPS to start:

```text
2 CPU
2-4 GB RAM
Ubuntu 22.04 or 24.04
```

Install Docker and Docker Compose on the VPS.

## 2. Copy The Project

Clone the repository on the VPS:

```bash
git clone <your-repository-url>
cd <your-repository-folder>/pizza-system
```

## 3. Configure Production Environment

Create a production env file:

```bash
cp .env.prod.example .env.prod
```

Edit `.env.prod`:

```env
POSTGRES_PASSWORD=use-a-real-password
PUBLIC_ORIGIN=http://your-vps-ip
FRONTEND_PORT=80
```

If you have a domain, use it instead:

```env
PUBLIC_ORIGIN=https://pizza.your-domain.com
```

## 4. Start The App

```bash
docker compose --env-file .env.prod -f docker-compose.prod.yml up -d --build
```

Open:

```text
http://your-vps-ip
```

or your domain:

```text
https://pizza.your-domain.com
```

## 5. Check Containers

```bash
docker ps
docker logs pizza-frontend --tail 100
docker logs pizza-task-service --tail 100
docker logs pizza-notification-service --tail 100
```

## 6. DNS

If you use a domain, create an `A` record:

```text
Type: A
Name: pizza
Value: <your-vps-public-ip>
```

Then `pizza.your-domain.com` points to the VPS.

## 7. HTTPS

The production compose file exposes the frontend on HTTP. For HTTPS, add a reverse proxy such as Caddy, Traefik, Nginx Proxy Manager, or Certbot in front of the frontend service.

Recommended next step:

```text
Caddy reverse proxy with automatic Let's Encrypt certificates
```