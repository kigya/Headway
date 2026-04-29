# Deploy Headway server services on Render

Microservices use **plain HTTP** client-side (`URLProtocol.HTTP`). Gateway resolves downstream hosts from env vars (`HOME_SERVICE_HOST`, `HOME_SERVICE_PORT`, etc.). The home service must return the current JSON contract (including `display_name`, `access_role`, `avatar_url` in the home screen response) for the gateway version you run.

## 1. Create (or update) the **home** Web Service

- **Root directory:** `server` (monorepo; Gradle and `gradlew` live here).
- **Dockerfile path:** `docker/render/Home.Dockerfile`
- **Plan:** at least the same region as gateway (for [private network](https://render.com/docs/private-network) if you use it).

### Home — environment variables

| Variable | Required | Notes |
|----------|----------|--------|
| `ENV` | yes | `prod` for production. |
| `HOME_SERVICE_SELF_HOST` | yes | Use `0.0.0.0` so the process accepts external connections. |
| `HOME_ICONS_PUBLIC_BASE_URL` | yes in prod | Public base URL for section icon assets (must be non-blank when `ENV=prod`). |
| `HOME_SERVICE_PORT` | no | If omitted, the listen port falls back to Render’s `PORT` (supported in code). |
| `PORT` | usually automatic | Render injects this; it overrides `HOME_SERVICE_PORT` for **listening** when set. |

After deploy, note the **internal hostname** (private network) or public host if you call home over the public URL only (not typical for gateway→home).

## 2. Point **gateway** at home

On the gateway Web Service, set:

| Variable | Required | Notes |
|----------|----------|--------|
| `HOME_SERVICE_HOST` | yes | Hostname or IP of the **home** service as seen **from the gateway container**. With Render private networking, use the home service’s **private** hostname (see Render docs for your account). |
| `HOME_SERVICE_PORT` | yes | Port on which **home** listens (same value as home’s effective listen port, usually the same as home’s `PORT`, often `10000` on Render). |
| `GATEWAY_SERVICE_HOST` | yes | `0.0.0.0` for public web services. |
| `GATEWAY_SERVICE_PORT` | no | If omitted, gateway listens on Render’s `PORT` when set. |
| `AUTH_SERVICE_*`, `DATABASE_SERVICE_*` | yes | Same as today; must match your auth/database services. |

**Important:** On the **gateway** service, `PORT` is only for the gateway process binding. It must **not** replace `HOME_SERVICE_PORT`: `HOME_SERVICE_PORT` is the **outbound** port used to call home.

## 3. Deploy order

1. Deploy **home** and confirm `/internal/v1/home/healthz` (under the service base URL) responds.
2. Set gateway `HOME_SERVICE_HOST` and `HOME_SERVICE_PORT` to reach that instance.
3. Deploy **gateway** (same git revision as home if you changed the home API).

## 4. Example env files

Copy from:

- `env.home.render.example`
- `env.gateway.render.example`

Replace placeholders (`your-auth-host`, CDN URL, internal home hostname) before pasting into Render.

## 5. Blueprint (`render.yaml`)

The repository root `render.yaml` is a starter [Render Blueprint](https://render.com/docs/blueprint-spec) for **home** and **gateway** (same `region` and `plan` — change as needed). Fill in `sync: false` variables in the Render dashboard. `HOME_SERVICE_HOST` / `HOME_SERVICE_PORT` on gateway use `fromService` so they track the **home** Web Service on the private network.

If you add auth/database Web Services later, extend the Blueprint or set `AUTH_SERVICE_*` / `DATABASE_SERVICE_*` manually.
