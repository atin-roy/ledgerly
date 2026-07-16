# Deployment — VPS with Caddy + Docker Compose

Every push to `main` deploys automatically via GitHub Actions. This document describes how the pieces fit and how to operate them.

## Architecture

```
push to main
   └─▶ GitHub Actions
         ├── verify   : backend tests (Gradle/H2), frontend lint + build
         ├── build    : docker build → push to ghcr.io/atin-roy/ledgerly-{api,web}
         └── deploy   : SSH to VPS → git pull → compose pull → compose up -d

Internet → Caddy (TLS, shared container)
   ├── ledgerly.atinroy.com/*      → ledgerly-web:3000   (Next.js)
   └── ledgerly.atinroy.com/api/*  → ledgerly-api:8080   (Spring Boot)
                                        └── ledgerly-postgres:5432
```

The frontend is built with `NEXT_PUBLIC_API_BASE_URL=/api`, so browser API calls are same-origin — Caddy splits traffic by path and CORS never fires in production.

## Layout on the VPS

```
/srv/
├── infra/caddy/           # shared Caddy container + Caddyfile (all apps)
└── apps/ledgerly/         # clone of this repo
    ├── docker-compose.prod.yml
    └── .env               # secrets — never committed (template: .env.production.example)
```

Containers join two Docker networks: the project-private `ledgerly_default` (api ↔ postgres) and the external `shared` network, which is how the Caddy container reaches `ledgerly-web` and `ledgerly-api` by container name. Nothing binds host ports; Caddy is the only entry point.

## GitHub repository secrets

| Secret        | Purpose                                      |
| ------------- | -------------------------------------------- |
| `VPS_HOST`    | VPS IP address                               |
| `VPS_USER`    | SSH user                                     |
| `VPS_SSH_KEY` | Private key of a dedicated deploy keypair    |

Image pulls on the VPS use a persistent `docker login ghcr.io` (stored in `~/.docker/config.json`). If pulls start failing with 401/denied, re-login on the VPS with a token that has `read:packages`.

## Caddy vhost

In `/srv/infra/caddy/Caddyfile`:

```
ledgerly.atinroy.com {
    import common_headers

    handle /api/* {
        reverse_proxy ledgerly-api:8080
    }

    handle {
        reverse_proxy ledgerly-web:3000
    }
}
```

Reload after edits: `docker exec caddy caddy reload --config /etc/caddy/Caddyfile`

## Manual operations

```bash
cd /srv/apps/ledgerly

docker compose -f docker-compose.prod.yml logs -f ledgerly-api   # logs
docker compose -f docker-compose.prod.yml ps                     # status
docker compose -f docker-compose.prod.yml up -d                  # manual deploy (after pull)
docker compose -f docker-compose.prod.yml down                   # stop (data survives in volume)
```

Rollback: images are also tagged `sha-<commit>`, so pin a known-good tag:

```bash
IMAGE_OWNER=atin-roy docker compose -f docker-compose.prod.yml up -d \
  --no-deps ledgerly-api ledgerly-web  # after editing the tag in the compose file
```

## Notes

- Flyway runs migrations automatically on backend startup (`SPRING_PROFILES_ACTIVE=prod` is baked into the image).
- `DATABASE_URL` must use the `jdbc:postgresql://` prefix.
- Postgres data lives in the `ledgerly-pgdata` volume; back it up with `docker exec ledgerly-postgres pg_dump -U ledgerly ledgerly > backup.sql`.
