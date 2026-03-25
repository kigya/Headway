---
icon: bolt
---

# Быстрый старт

## Репозиторий

[github.com/kigya/Headway](https://github.com/kigya/Headway)

## Клонирование

1. В Android Studio: **Get from VCS** (или **File → New → Project from Version Control…**).
2. URL: `https://github.com/kigya/Headway.git`.
3. Выберите папку и выполните **Clone** (при необходимости войдите в GitHub или используйте токен).

## Git: имя и email

В терминале IDE:

Глобально (для всех репозиториев):

```
git config --global user.name "Ваше Имя"
git config --global user.email "you@example.com"
```

Только для этого репозитория (из корня клона):

```
git config user.name "Ваше Имя"
git config user.email "you@example.com"
```

Проверка:

```
git config --list --show-origin
```

## Где что лежит в монорепозитории

* `client/` — KMP-приложение (Android, iOS, Desktop, Web), Compose, MVIKotlin.
* `server/` — Ktor-сервисы (auth, database, home, gateway, admin и др.), Gradle, Docker.
* `AGENTS.md` (корень) — общие правила Kotlin для всего репо.
* `client/AGENTS.md` и `server/AGENTS.md` — детали по сторонам.
* `.cursor/rules/` и `.cursor/skills/` — подсказки для IDE и агентов (не заменяют `AGENTS.md`).

## Минимальные команды после правок

**Клиент** (основная проверка перед сдачей задачи):

```
cd client && ./gradlew app:headwayAndroid:assembleDebug
cd client && ./gradlew detekt
```

**Сервер:**

```
cd server && ./gradlew build
cd server && ./gradlew detekt
```

Дальше — настройка хуков, доски задач и соглашений по веткам: раздел [Git, PR, метки и доска задач](git-and-board.md).
