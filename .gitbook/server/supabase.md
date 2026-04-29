---
icon: database
---

# Supabase и Postgres

Продакшен и разработка используют **хостованный PostgreSQL в Supabase**. Схема и данные — зона ответственности серверной части; миграции лежат в репозитории (`server/database/migrations/` и правила в `server/AGENTS.md`).

## Консоли проектов

| Окружение | Project ref | Ссылка на дашборд |
|-----------|-------------|-------------------|
| **Dev** | `ymqpiogjzymkkwzutqds` | [Supabase — dev](https://supabase.com/dashboard/project/ymqpiogjzymkkwzutqds) |
| **Prod** | `lhdkfjlltgnxrttrqfla` | [Supabase — prod](https://supabase.com/dashboard/project/lhdkfjlltgnxrttrqfla) |

Те же ссылки продублированы в [`.ai/cursor/RESOURCES.md`](https://github.com/kigya/Headway/blob/trunk/.ai/cursor/RESOURCES.md) для агентов IDE.

## Практические заметки

* Строки подключения и ключи API не храните в git; для локальной работы используйте переменные окружения и/или результат **githubEnvSync** по политике команды.
* Изменения схемы сопровождайте миграциями и согласованием с backend-частью.
* Окружение в коде сервера различайте через типизированные помощники (`CommonConfigurationValues.environment` и т.п.), а не сырые сравнения строк — см. `server/AGENTS.md` и корневой `AGENTS.md`.

## Связь с GitHub Variables

Значения для dev/prod часто задаются в **GitHub Environments** и подтягиваются в docker-env через [githubEnvSync](github-env-sync.md). При добавлении новой переменной для сервиса обычно нужно: завести её в GitHub, при необходимости добавить плейсхолдер в соответствующий `*.template` в `server/docker/template/`, выполнить синхронизацию.
