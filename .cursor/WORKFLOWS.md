# Headway Cursor agent workflows

This repo layers **Agent Skills** on top of [`.cursor/rules/`](rules/) and root [`AGENTS.md`](../AGENTS.md), [`client/AGENTS.md`](../client/AGENTS.md), [`server/AGENTS.md`](../server/AGENTS.md). Nothing here runs in the background: you invoke workflows from chat.

## Skill: `headway-rule-aware-workflow`

**What it does:** Steers implementation toward the closest `AGENTS.md` and the matching `.mdc` rules (`global`, `client` or `server` by path, `detekt-guardrails` for Kotlin, `lessons-learned` for cumulative mistakes). Use via `/headway-rule-aware-workflow` or `@headway-rule-aware-workflow` when you want explicit rule alignment while coding.

## `/capture-lesson`

**When:** After a meaningful correction—especially when the same mistake could recur (architecture, boundaries, stable patterns).

**What happens:** The agent classifies the mistake as reusable or one-off. Only reusable mistakes update [`rules/lessons-learned.mdc`](rules/lessons-learned.mdc), merging with an existing lesson when the root cause matches.

## `/finish-feature`

**When:** Before merge/PR or when wrapping a feature or fix.

**What happens:** The agent summarizes which rules mattered for this change, what to verify (Gradle + detekt by area), drift risks, and—only if needed—a **suggestion** to promote stable guidance into `AGENTS.md`. It never edits `AGENTS.md` for you.

## `/commit`

**When:** You want to record local changes on the current branch in one commit.

**What happens:** The agent runs `git add -A` (or `git add` on paths you named), builds the one-line message `CLIENT-HEADWAY-<N>:` / `SERVER-HEADWAY-<N>:` / `FULLSTACK-HEADWAY-<N>:` from the branch name and staged paths, then runs `git commit`. It does **not** push unless you ask in the same message.

## `/pull-request`

**When:** You want the branch on `origin` and an open PR into `trunk` (or to refresh an existing PR).

**What happens:** The agent pushes the current branch, finds an open PR for that head → `trunk` or creates one, then updates **title**, **body** (including `Closes #<task>` for the Development sidebar when the task number is parsed from the branch), and **labels** (`headway-client` / `headway-server` / `headway-fullstack`, plus type labels). GitHub Projects (board columns) are optional via `gh` if available; otherwise it may ask you to update the project manually. **Note:** `Closes #N` closes the issue when the PR merges into the repo **default** branch.

## What “reusable” means here

**Reusable (capture):** Violates boundaries or patterns already described in `AGENTS.md` or `.cursor/rules`; likely to recur; spans more than a one-off; can be stated as a short general rule.

**Not reusable:** Typos; one-off product copy; data specific to a single screen; renames with no broader pattern; isolated cases with no rule value.

## Promotion to `AGENTS.md`

- **Manual only.** Editors decide what becomes long-term policy.
- Prefer promoting **stable, recurring** rules that belong in canonical docs—not every lesson, not bulk moves.
- `/finish-feature` may **suggest** 1–3 promotion candidates when `lessons-learned` is bloated; you still edit `AGENTS.md` yourself if you agree.

**Bloat signals:** More than 12 lessons in `lessons-learned.mdc`, or three or more lessons clustering on the same theme (e.g. Freud vs Material3, ViewModel vs Store, navigation, gateway/`upstreamCall`, detekt/style, client `Outcome`).
