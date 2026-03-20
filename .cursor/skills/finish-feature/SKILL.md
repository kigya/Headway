---
name: finish-feature
description: >-
  Headway feature wrap-up: critical .cursor/rules, verification commands
  (client/server Gradle + detekt), drift risks, and optional AGENTS.md
  promotion hints. Use when the user runs /finish-feature before merge or PR.
disable-model-invocation: true
---

# Finish feature

## Instructions

1. Infer **scope** from paths touched in this task: `client/**`, `server/**`, root/shared Kotlin, design-system, etc.
2. **Critical rules (2–6 bullets only):** Pick the most relevant points from [.cursor/rules/global.mdc](.cursor/rules/global.mdc), [.cursor/rules/client.mdc](.cursor/rules/client.mdc) or [.cursor/rules/server.mdc](.cursor/rules/server.mdc), [.cursor/rules/detekt-guardrails.mdc](.cursor/rules/detekt-guardrails.mdc). Cite [.cursor/rules/lessons-learned.mdc](.cursor/rules/lessons-learned.mdc) only when a lesson clearly matches the change.
3. **Checks / expected before completion** (state that the agent does not run CI; these are for the human or an explicit agent run):
   - **Client substantive work:** `cd client && ./gradlew app:headwayAndroid:assembleDebug` and `cd client && ./gradlew detekt` per [client/AGENTS.md](client/AGENTS.md).
   - **Server substantive work:** `cd server && ./gradlew build` and `cd server && ./gradlew detekt` per [server/AGENTS.md](server/AGENTS.md). If the diff is narrowly scoped to one module and [server/AGENTS.md](server/AGENTS.md) lists a tighter task, you may mention that alternative.
4. **Drift risks:** If the change pattern conflicts with a cited rule, add 1–3 bullets; otherwise omit this section.
5. **Lessons file bloat:** Read [.cursor/rules/lessons-learned.mdc](.cursor/rules/lessons-learned.mdc). Count lessons (`### N` sections). **Bloat if** count **> 12** **or** **≥ 3** lessons cluster on one theme, e.g. Freud/Material3, ViewModel vs Store, navigation/NavigatorContract, gateway/`upstreamCall`, detekt/style, client `Outcome`.
6. If bloat: add a **Promotion reminder** only—suggest **1–3 stable, recurring** lessons that could move into long-form `AGENTS.md` guidance. Do **not** edit any `AGENTS.md` file. Do **not** suggest promoting everything.

## Output format

Produce a concise report with these sections in order:

1. **Critical rules used**
2. **Checks performed / expected before completion**
3. **Drift risks** (omit if none)
4. **Promotion reminder** (only if bloat condition met)

## Constraints

- No hooks, watchers, or automation scaffolding.
- No edits to [AGENTS.md](AGENTS.md), [client/AGENTS.md](client/AGENTS.md), or [server/AGENTS.md](server/AGENTS.md).
