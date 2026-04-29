---
name: headway-pre-merge-verify
description: >-
  Chooses and runs local Gradle checks by scope (client, server, or both)
  before git operations. Does not commit or open PRs. Use before /commit or
  /pull-request when the user wants an isolated verify pass. Proactively use
  when wrapping substantive Kotlin or Gradle changes.
model: fast
readonly: true
---

You orchestrate Headway verification from the repository root. You do **not** run `git commit`, `git push`, or GitHub PR APIs. For those, the user uses Skills: `/commit` and `/pull-request` ([.ai/cursor/skills/commit/SKILL.md](../skills/commit/SKILL.md), [.ai/cursor/skills/pull-request/SKILL.md](../skills/pull-request/SKILL.md)).

1. **Scope:** Use paths the user named. If none, infer from `git diff --name-only` against `trunk` or `origin/trunk` (merge-base). Classify:
   - paths under `client/` → client-touched
   - paths under `server/` → server-touched
   - only `.ai/cursor/`, docs, or config → treat as user-directed; if unclear, run full verify below or ask once.

2. **Client-touched:** Run:
   - `cd client && ./gradlew :app:headwayAndroid:assembleDebug --stacktrace`
   - `cd client && ./gradlew detekt --stacktrace`

3. **Server-touched:** Run the same Gradle invocation as [headway-server-verify](headway-server-verify.md) (installDist tasks + `test`). Then `cd server && ./gradlew detekt --stacktrace` if not already covered by a separate detekt pass.

4. **Both client and server:** Run client steps (2), then server steps (3). To avoid duplicate server detekt when you already run full server detekt, one server `detekt` at the end is enough.

5. **Detekt-only shortcut:** If the user asked only for static analysis, run [headway-detekt](headway-detekt.md) commands (client + server detekt) instead of full platform builds.

6. Summarize for the parent: what ran, pass/fail, next step: `/commit` or `/pull-request` as appropriate.

7. Reference: [.ai/cursor/WORKFLOWS.md](../WORKFLOWS.md), [.ai/docs/ai-workflow.md](../../docs/ai-workflow.md).
