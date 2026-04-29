# Headway Cursor agent workflows

This repo layers **Agent Skills** on top of [`.ai/cursor/rules/`](rules/), [`.ai/cursor/memory-bank/`](memory-bank/), and root [`AGENTS.md`](../../AGENTS.md), [`client/AGENTS.md`](../../client/AGENTS.md), [`server/AGENTS.md`](../../server/AGENTS.md). Nothing here runs in the background: you invoke workflows from chat.

**How this fits Spec Kit:** Spec-driven commands live under [`.ai/cursor/commands/`](commands/) (`speckit.*`). See [.ai/docs/ai-workflow.md](../docs/ai-workflow.md) for the full model.

**Optional MCP tooling:** [`.ai/cursor/mcp.json`](mcp.json) registers **Serena** (symbol-aware navigation and edits) and **Repomix** (compact codebase snapshots). Neither replaces Memory Bank, Spec Kit, or `AGENTS.md`; see [.ai/docs/ai-workflow.md](../docs/ai-workflow.md) → *Serena and Repomix*.

## Subagents ([`.ai/cursor/agents/`](agents/))

Cursor **Subagents** are separate agent contexts for noisy or scoped work (Gradle logs, CI-parity checks). Invoke with `/name` (e.g. `/headway-detekt`) or natural language. They complement built-in Explore / Bash / Browser; they do **not** replace **Skills** for one-shot git flows.

| Subagent | Role |
|----------|------|
| `headway-detekt` | Client + server `./gradlew detekt` |
| `headway-server-verify` | Server `installDist` targets + `test` (CI parity); optional server detekt |
| `headway-client-android-build` | `:app:headwayAndroid:assembleDebug` |
| `headway-client-desktop-build` | `:app:headwayDesktop:packageDistributionForCurrentOS` + OS prereqs |
| `headway-client-web-build` | `:app:headwayWeb:wasmJsBrowserDistribution` + Node/Corepack/Yarn |
| `headway-client-ios-build` | `xcodebuild` simulator, no signing (macOS) |
| `headway-pre-merge-verify` | Scope-based checks before git; **does not** commit or open PRs |

**Git:** Use **`/commit`** and **`/pull-request`** (Skills), not a duplicate subagent. `headway-pre-merge-verify` ends by pointing to those when appropriate.

## Skill: `headway-rule-aware-workflow`

**What it does:** Steers implementation toward the closest `AGENTS.md`, the matching `.mdc` rules (`global`, `client` or `server` by path, `detekt-guardrails` for Kotlin, `lessons-learned` for cumulative mistakes), and a quick read of **Memory Bank** (`activeContext.md`, `systemPatterns.md` when relevant). Use via `/headway-rule-aware-workflow` or `@headway-rule-aware-workflow` when you want explicit rule alignment while coding.

## `/capture-lesson`

**When:** After a meaningful correction—especially when the same mistake could recur (architecture, boundaries, stable patterns).

**What happens:** The agent classifies the mistake as reusable or one-off. Only reusable mistakes update [`rules/lessons-learned.mdc`](rules/lessons-learned.mdc), merging with an existing lesson when the root cause matches. When the lesson implies ongoing context (e.g. a new stack rule or pattern), the agent updates the relevant **Memory Bank** file(s) briefly or notes what the human should promote—without duplicating the full lesson text.

## `/finish-feature`

**When:** Before merge/PR or when wrapping a feature or fix.

**What happens:** The agent summarizes which rules mattered for this change, what to verify (Gradle + detekt by area), drift risks, and—only if needed—a **suggestion** to promote stable guidance into `AGENTS.md`. It never edits `AGENTS.md` for you. It also performs a **Memory Bank drift check**: refresh [`memory-bank/activeContext.md`](memory-bank/activeContext.md) when the branch or focus changed; append a line to [`memory-bank/progress.md`](memory-bank/progress.md) when workflow or integration work landed; update [`memory-bank/systemPatterns.md`](memory-bank/systemPatterns.md) or [`memory-bank/techContext.md`](memory-bank/techContext.md) only for stable, cross-cutting facts.

## `/commit`

**When:** You want to record local changes on the current branch in one commit.

**What happens:** The agent runs `git add -A` (or `git add` on paths you named), builds the one-line message `CLIENT-HEADWAY-<N>:` / `SERVER-HEADWAY-<N>:` / `FULLSTACK-HEADWAY-<N>:` from the branch name and staged paths, then runs `git commit`. It does **not** push unless you ask in the same message.

## `/pull-request`

**When:** You want the branch on `origin` and an open PR into `trunk` (or to refresh an existing PR).

**What happens:** The agent pushes the current branch, finds an open PR for that head → `trunk` or creates one, then updates **title**, **body** (including `Closes #<task>` for the Development sidebar when the task number is parsed from the branch), and **labels** (`headway-client` / `headway-server` / `headway-fullstack`, plus type labels). GitHub Projects (board columns) are optional via `gh` if available; otherwise it may ask you to update the project manually. **Note:** `Closes #N` closes the issue when the PR merges into the repo **default** branch.

## What “reusable” means here

**Reusable (capture):** Violates boundaries or patterns already described in `AGENTS.md` or `.ai/cursor/rules`; likely to recur; spans more than a one-off; can be stated as a short general rule.

**Not reusable:** Typos; one-off product copy; data specific to a single screen; renames with no broader pattern; isolated cases with no rule value.

## Promotion to `AGENTS.md`

- **Manual only.** Editors decide what becomes long-term policy.
- Prefer promoting **stable, recurring** rules that belong in canonical docs—not every lesson, not bulk moves.
- `/finish-feature` may **suggest** 1–3 promotion candidates when `lessons-learned` is bloated; you still edit `AGENTS.md` yourself if you agree.

**Bloat signals:** More than 12 lessons in `lessons-learned.mdc`, or three or more lessons clustering on the same theme (e.g. Freud vs Material3, ViewModel vs Store, navigation, gateway/`upstreamCall`, detekt/style, client `Outcome`).
