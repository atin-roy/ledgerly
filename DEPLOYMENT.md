# Deployment Guide — VPS with Caddy + Docker Compose

## Architecture

```
Internet → Caddy (reverse proxy, TLS) → Docker network
                                            ├── backend  (Spring Boot :8080)
                                            ├── frontend (Next.js :3000)
                                            └── db       (PostgreSQL :5432)
```

Caddy handles HTTPS automatically via Let's Encrypt. Each project on the VPS gets its own `docker-compose.yml`; Caddy is shared across projects.

## Directory layout on the VPS

```
/srv/
├── caddy/
│   └── Caddyfile          ← shared across all projects
└── ledgerly/
    ├── docker-compose.yml
    └── .env               ← secrets (not committed)
```

## Environment variables

Create `/srv/ledgerly/.env`:

```env
# Database
DATABASE_URL=jdbc:postgresql://db:5432/ledgerly
DATABASE_USERNAME=ledgerly
DATABASE_PASSWORD=<strong-password>

# JWT
JWT_SECRET=<at-least-64-char-random-string>

# CORS — set to your actual frontend domain
CORS_ALLOWED_ORIGINS=https://ledgerly.yourdomain.com

# Postgres container
POSTGRES_DB=ledgerly
POSTGRES_USER=ledgerly
POSTGRES_PASSWORD=<same-as-DATABASE_PASSWORD>
```

## `docker-compose.yml`

```yaml
services:
  db:
    image: postgres:16-alpine
    restart: unless-stopped
    env_file: .env
    volumes:
      - pgdata:/var/lib/postgresql/data
    networks:
      - ledgerly

  backend:
    image: ghcr.io/<your-username>/ledgerly-backend:latest
    restart: unless-stopped
    env_file: .env
    environment:
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      - db
    networks:
      - ledgerly

  frontend:
    image: ghcr.io/<your-username>/ledgerly-frontend:latest
    restart: unless-stopped
    environment:
      NEXT_PUBLIC_API_BASE_URL: https://api.ledgerly.yourdomain.com/api
    networks:
      - ledgerly

volumes:
  pgdata:

networks:
  ledgerly:
    name: ledgerly
```

## `Caddyfile` (add to shared Caddy config)

```
ledgerly.yourdomain.com {
    reverse_proxy frontend:3000
}

api.ledgerly.yourdomain.com {
    reverse_proxy backend:8080
}
```

> Make sure the Caddy container is on the `ledgerly` Docker network:
> `docker network connect ledgerly caddy`

## Deploy

```bash
# First deploy
cd /srv/ledgerly
docker compose pull
docker compose up -d

# Update
docker compose pull
docker compose up -d --no-deps backend frontend
```

## Notes

- Flyway runs automatically on backend startup when `SPRING_PROFILES_ACTIVE=prod`.
- The `DATABASE_URL` must use the `jdbc:postgresql://` prefix (not the legacy `postgres://` format).
- To view logs: `docker compose logs -f backend`
