---
name: headway-rule-aware-workflow
description: >-
  Aligns Headway Kotlin edits with the closest AGENTS.md and .cursor/rules
  (global, client/server, detekt-guardrails, lessons-learned). Use for KMP
  Compose, MVIKotlin Store vs ViewModel, Navigation3, Freud design system,
  client Outcome, server gateway upstreamCall, and shared Kotlin style.
---

# Headway rule-aware workflow

## Instructions

1. Determine the **closest** source of truth by path:
   - Repo root / shared Kotlin → [AGENTS.md](AGENTS.md)
   - `client/**` → [client/AGENTS.md](client/AGENTS.md)
   - `server/**` → [server/AGENTS.md](server/AGENTS.md)
   - Design-system code → [client/core/design-system/.../AGENTS.md](client/core/design-system/src/commonMain/kotlin/dev/kigya/headway/core/designSystem/AGENTS.md)
2. Load matching Cursor rules from [.cursor/rules/](.cursor/rules/):
   - Always: [global.mdc](.cursor/rules/global.mdc), [lessons-learned.mdc](.cursor/rules/lessons-learned.mdc)
   - Under `client/**`: [client.mdc](.cursor/rules/client.mdc), [detekt-guardrails.mdc](.cursor/rules/detekt-guardrails.mdc)
   - Under `server/**`: [server.mdc](.cursor/rules/server.mdc), [detekt-guardrails.mdc](.cursor/rules/detekt-guardrails.mdc)
   - Kotlin anywhere: consider [detekt-guardrails.mdc](.cursor/rules/detekt-guardrails.mdc)
3. Prefer **existing** project patterns over inventing new ones; treat [lessons-learned.mdc](.cursor/rules/lessons-learned.mdc) as cumulative anti-patterns to avoid.
4. Do **not** duplicate long AGENTS text in chat—apply it. Do **not** add hooks, CI, or background automation.
5. For recording a mistake after a fix, the user should run `/capture-lesson`. For pre-merge wrap-up, `/finish-feature`.

## When to use

- Implementing or reviewing Headway client/server Kotlin changes.
- The user invokes `/headway-rule-aware-workflow` or `@headway-rule-aware-workflow`.
- Ambiguity about MVI boundaries, navigation, Freud vs Material3, gateway HTTP, or detekt scope.
