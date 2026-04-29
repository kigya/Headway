---
name: finish-feature
description: >-
  Headway feature wrap-up: critical .ai/cursor/rules, verification commands
  (client/server Gradle + detekt), drift risks, Memory Bank maintenance, and
  optional AGENTS.md promotion hints. Use when the user runs /finish-feature
  before merge or PR.
disable-model-invocation: true
---

# Finish feature

## Instructions

1. Infer **scope** from paths touched in this task: `client/**`, `server/**`, root/shared Kotlin, design-system, `.ai/cursor/**`, `.ai/specify/**`, `docs/**`, etc.
2. Read [.ai/cursor/memory-bank/activeContext.md](../../memory-bank/activeContext.md) for stated focus; note conflicts with the actual diff if any.
3. **Critical rules (2–6 bullets only):** Pick the most relevant points from [.ai/cursor/rules/global.mdc](../../rules/global.mdc), [.ai/cursor/rules/client.mdc](../../rules/client.mdc) or [.ai/cursor/rules/server.mdc](../../rules/server.mdc), [.ai/cursor/rules/detekt-guardrails.mdc](../../rules/detekt-guardrails.mdc). Cite [.ai/cursor/rules/lessons-learned.mdc](../../rules/lessons-learned.mdc) only when a lesson clearly matches the change.
4. **Checks / expected before completion** (state that the agent does not run CI; these are for the human or an explicit agent run):
   - **Client substantive work:** `cd client && ./gradlew app:headwayAndroid:assembleDebug` and `cd client && ./gradlew detekt` per [client/AGENTS.md](../../../client/AGENTS.md).
   - **Server substantive work:** `cd server && ./gradlew build` and `cd server && ./gradlew detekt` per [server/AGENTS.md](../../../server/AGENTS.md). If the diff is narrowly scoped to one module and [server/AGENTS.md](../../../server/AGENTS.md) lists a tighter task, you may mention that alternative.
   - **Isolated verification:** Optional Cursor subagents under [.ai/cursor/agents/](../../agents/) (e.g. `/headway-pre-merge-verify`, `/headway-detekt`, `/headway-server-verify`) per [.ai/cursor/WORKFLOWS.md](../../WORKFLOWS.md).
5. **Drift risks:** If the change pattern conflicts with a cited rule, add 1–3 bullets; otherwise omit this section.
6. **Memory Bank drift:** Update [.ai/cursor/memory-bank/](../../memory-bank/) when this session changed durable context:
   - [`activeContext.md`](../../memory-bank/activeContext.md) — current branch intent / next steps if they shifted.
   - [`progress.md`](../../memory-bank/progress.md) — one dated bullet for notable integration or workflow changes.
   - [`systemPatterns.md`](../../memory-bank/systemPatterns.md) or [`techContext.md`](../../memory-bank/techContext.md) — only for **stable**, **cross-cutting** facts; skip one-off UI copy or single-file trivia.
7. **Lessons file bloat:** Read [.ai/cursor/rules/lessons-learned.mdc](../../rules/lessons-learned.mdc). Count lessons (`### N` sections). **Bloat if** count **> 12** **or** **≥ 3** lessons cluster on one theme, e.g. Freud/Material3, ViewModel vs Store, navigation/NavigatorContract, gateway/`upstreamCall`, detekt/style, client `Outcome`.
8. If bloat: add a **Promotion reminder** only—suggest **1–3 stable, recurring** lessons that could move into long-form `AGENTS.md` guidance. Do **not** edit any `AGENTS.md` file. Do **not** suggest promoting everything.

## Output format

Produce a concise report with these sections in order:

1. **Critical rules used**
2. **Checks performed / expected before completion**
3. **Drift risks** (omit if none)
4. **Memory Bank** (omit if nothing to update—otherwise list files touched and one line each)
5. **Promotion reminder** (only if bloat condition met)

## Constraints

- No hooks, watchers, or automation scaffolding.
- No edits to [AGENTS.md](../../../AGENTS.md), [client/AGENTS.md](../../../client/AGENTS.md), or [server/AGENTS.md](../../../server/AGENTS.md).
