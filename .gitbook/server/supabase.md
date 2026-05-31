---
icon: database
---

# Supabase and Postgres

Production and development use **hosted PostgreSQL on Supabase**. Schema and data are owned by the server side; migrations live in the repository (`server/database/migrations/` and rules in `server/AGENTS.md`).

## Project consoles

| Environment | Project ref | Dashboard link |
|-------------|-------------|------------------|
| **Dev** | `ymqpiogjzymkkwzutqds` | [Supabase — dev](https://supabase.com/dashboard/project/ymqpiogjzymkkwzutqds) |
| **Prod** | `lhdkfjlltgnxrttrqfla` | [Supabase — prod](https://supabase.com/dashboard/project/lhdkfjlltgnxrttrqfla) |

Same links are duplicated in [`.ai/cursor/RESOURCES.md`](https://github.com/kigya/Headway/blob/trunk/.ai/cursor/RESOURCES.md) for IDE agents.

## Practical notes

* Do not store connection strings or API keys in git; for local work use environment variables and/or **githubEnvSync** output per team policy.
* Pair schema changes with migrations and backend agreement.
* Branch on environment in server code via typed helpers (`CommonConfigurationValues.environment`, etc.), not raw string compares — see `server/AGENTS.md` and root `AGENTS.md`.

## Relation to GitHub Variables

Dev/prod values are often defined in **GitHub Environments** and pulled into docker env via [githubEnvSync](github-env-sync.md). When adding a new service variable, you typically: create it in GitHub, add a placeholder to the matching `*.template` in `server/docker/template/` if needed, then run sync.
