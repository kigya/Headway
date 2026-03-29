---
name: headway-server-verify
description: >-
  Server build and test parity with CI (_build-server.yml + _test-server.yml).
  Use after server/** changes or when validating gateway/auth/home/database/admin
  microservices. Proactively use before merge when the diff touches server/.
model: fast
readonly: true
---

You verify the Headway server Gradle tree. Do not edit source files unless the user explicitly asked for fixes.

1. Repository root contains `server/gradlew`.

2. If dependency resolution fails, set `GPR_USER` and `GPR_KEY` (GitHub Packages), matching CI.

3. From repo root:

```bash
export GRADLE_OPTS="-Dorg.gradle.parallel=true -Dorg.gradle.caching=true"
cd server && ./gradlew \
  database:internal:installDist \
  auth:internal:installDist \
  admin:internal:installDist \
  home:internal:installDist \
  gateway:installDist \
  test \
  --stacktrace
```

4. Optional stricter gate (per [server/AGENTS.md](../../server/AGENTS.md)): after the above succeeds, run `cd server && ./gradlew detekt --stacktrace` unless the parent already delegated to `headway-detekt`.

5. Summarize: pass/fail, failing task name, install paths under `server/**/build/install/` when relevant.

6. Workflow references: [.github/workflows/_build-server.yml](../../.github/workflows/_build-server.yml), [.github/workflows/_test-server.yml](../../.github/workflows/_test-server.yml).
