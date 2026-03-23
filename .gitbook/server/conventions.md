---
icon: server
---

# Сервер: конвенции и архитектура

Полный канон — [`server/AGENTS.md`](https://github.com/kigya/Headway/blob/trunk/server/AGENTS.md). Здесь — кратко для ежедневной работы.

## Стек

Kotlin JVM 17, **Ktor** + Netty, **KGraphQL** на gateway, **Koin**, **Exposed** + PostgreSQL, **kotlinx.serialization**, Docker. Анализ: **Detekt** + ktlint.

## Команды

```
cd server && ./gradlew build
cd server && ./gradlew detekt
```

Для точечной сборки сервиса см. примеры в `server/AGENTS.md` (`:auth:internal:build`, `:gateway:build`, …). Есть агрегат **`verifyWithCoverage`** (detekt + Kover по правилам в `server/build.gradle.kts`).

## Структура

* Микросервис = **`service/api`** (контракт: DTO, `@Resource`, URL holder) + **`service/internal`** (роутинг, use case, репозитории, DI).
* **`gateway`** — единая точка для клиента (GraphQL), оркестрация вызовов auth/database и др.
* **`common`** — общие утилиты, healthz, сериализаторы, базовые Ktor-настройки.
* **`database/migrations/`** — SQL-миграции для хостового Postgres (Supabase).
* **`docker/`** — env-файлы и шаблоны для генерации; см. также [githubEnvSync](github-env-sync.md).

## Пакеты и видимость

Пространство имён: `dev.kigya.headway.…`. В `internal` модулях по умолчанию всё **`internal`**. В `api` типы обычно публичные как контракт.

## HTTP

Все пути через **Ktor `@Resource`**, без строковых литералов маршрутов. База сервиса: **`/internal/v1/<сервис>`**. У каждого сервиса под базой — **`/healthz`**.

## Слои внутри internal

* **Роутинг** — `trim()` / `isBlank()`, Ktor-исключения для базовой валидации, тонкая передача в use case.
* **Use case** — один класс на действие, `suspend operator fun invoke`, без HTTP и без SQL.
* **Репозиторий** — Exposed/HTTP; БД только через обёртки вроде `Database.dbQuery`.

## Ошибки

Запечатанные **`ServiceException`** с вариантами вроде `InvalidRequest`, `Unauthorized`, `DependencyUnavailable`, `UpstreamProtocol`. Новые случаи регистрируйте в **`handle<Service>Exceptions()`** в StatusPages.

## Gateway

Все исходящие HTTP-вызовы из репозиториев gateway — через **`upstreamCall(...)`**, не «голый» `httpClient.get/post`. Ошибки мапятся через **`toGatewayException`**, расширения GraphQL сохраняют коды и зависимости.

Публичные модели клиента живут в gateway; расхождения с DTO микросервисов чинят **мапперами**, а не правкой чужих `api`-моделей без необходимости.

## Docker и секреты

Секреты в git не кладём. Для локальной/generation см. [githubEnvSync](github-env-sync.md) и [Supabase](supabase.md). В prod обязателен осмысленный **`validateSecrets()`** при старте.

## Перед рискованными изменениями согласуйте

* новые зависимости;
* `build-logic/` и плагины;
* Docker/env и публичный API `common`;
* смену схемы БД (таблицы Exposed + миграции).

## Новый endpoint (очень кратко)

1. `api`: DTO, `@Resource`.
2. `internal`: use case, репозиторий при необходимости, роутинг, маппинг исключений, Koin.
3. Не дублировать модели — брать из другого `api` зависимостью.

Новая операция GraphQL: имя в `GatewayGraphqlOperation`, модель, маппер, репозиторий с `upstreamCall`, use case, схема, регистрация типов в `GatewayApi.kt` — детали в `server/AGENTS.md`.
