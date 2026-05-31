---
icon: server
---

# Server: conventions and architecture

Full canonical rules — [`server/AGENTS.md`](https://github.com/kigya/Headway/blob/trunk/server/AGENTS.md). Below is a short daily-work summary.

## Stack

Kotlin JVM 17, **Ktor** + Netty, **KGraphQL** on gateway, **Koin**, **Exposed** + PostgreSQL, **kotlinx.serialization**, Docker. Analysis: **Detekt** + ktlint.

## Commands

```
cd server && ./gradlew build
cd server && ./gradlew detekt
```

For a single service build, see examples in `server/AGENTS.md` (`:auth:internal:build`, `:gateway:build`, …). Aggregate task **`verifyWithCoverage`** (detekt + Kover per `server/build.gradle.kts`).

## Structure

* Microservice = **`service/api`** (contract: DTOs, `@Resource`, URL holder) + **`service/internal`** (routing, use cases, repositories, DI).
* **`gateway`** — single entry for the client (GraphQL), orchestrates auth/database and other calls.
* **`common`** — shared utilities, healthz, serializers, base Ktor setup.
* **`database/migrations/`** — SQL migrations for hosted Postgres (Supabase).
* **`docker/`** — env files and generation templates; see also [githubEnvSync](github-env-sync.md).

## Packages and visibility

Namespace: `dev.kigya.headway.…`. In `internal` modules, default everything to **`internal`**. In `api`, types are usually public as the contract.

## HTTP

All paths via **Ktor `@Resource`**, no string route literals. Service base: **`/internal/v1/<service>`**. Each service has **`/healthz`** under its base.

## Layers inside internal

* **Routing** — `trim()` / `isBlank()`, Ktor exceptions for basic validation, thin handoff to use cases.
* **Use case** — one class per action, `suspend operator fun invoke`, no HTTP and no SQL.
* **Repository** — Exposed/HTTP; DB only through wrappers like `Database.dbQuery`.

## Errors

Sealed **`ServiceException`** with variants such as `InvalidRequest`, `Unauthorized`, `DependencyUnavailable`, `UpstreamProtocol`. Register new cases in **`handle<Service>Exceptions()`** in StatusPages.

## Gateway

All outbound HTTP from gateway repositories goes through **`upstreamCall(...)`**, not bare `httpClient.get/post`. Map failures with **`toGatewayException`**; GraphQL extensions keep codes and dependencies.

Public client models live in gateway; fix drift from microservice DTOs with **mappers**, not by changing foreign `api` models unless necessary.

## Docker and secrets

Do not commit secrets. For local/generation see [githubEnvSync](github-env-sync.md) and [Supabase](supabase.md). In prod, require meaningful **`validateSecrets()`** at startup.

## Coordinate before risky changes

* new dependencies;
* `build-logic/` and plugins;
* Docker/env and public `common` API;
* schema changes (Exposed tables + migrations).

## New endpoint (very short)

1. `api`: DTO, `@Resource`.
2. `internal`: use case, repository if needed, routing, exception mapping, Koin.
3. Do not duplicate models — depend on another service’s `api`.

New GraphQL operation: name in `GatewayGraphqlOperation`, model, mapper, repository with `upstreamCall`, use case, schema, type registration in `GatewayApi.kt` — details in `server/AGENTS.md`.
