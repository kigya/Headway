# Active context

## Current branch intent

- `003-learning-questions-server` — deliver **spec 003** learning-questions flow: database service (catalog, progress, remarks, guest sessions), gateway GraphQL + auth policy, JWT guest integration, and related API contracts.

## Immediate next steps (for the human)

- Open or refresh the PR against `trunk`; run CI if required beyond local `./gradlew build` / `detekt`.
- Apply DB migration `server/database/migrations/V001_learning_questions.sql` in the target Postgres environment when deploying.
- Align GitHub issue number with `Closes #…` in the PR if it is not **#3**.

## Notes

- Agent rules: `.cursor/rules/` + closest `AGENTS.md`; cumulative lessons in `lessons-learned.mdc` (now includes §18 serialization, §19 `data/` layout vs `repository/`).
