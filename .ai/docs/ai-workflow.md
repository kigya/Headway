# AI workflow (Spec Kit + Memory Bank + Cursor)

This document describes how **AI-assisted development** is wired in Headway. It complements `AGENTS.md` (coding rules) and does not replace it.

## Where things live

| Layer | Purpose |
|-------|---------|
| **Closest `AGENTS.md`** | Canonical **code policy** by path (root, `client/`, `server/`, design-system). |
| **`.ai/cursor/rules/*.mdc`** | Short, enforceable agent rules derived from or pointing at `AGENTS.md`. |
| **`.ai/cursor/memory-bank/`** | Canonical **project context** for agents: orientation, current focus, stack summary, pattern reminders. |
| **`.ai/cursor/rules/lessons-learned.mdc`** | Curated **reusable mistakes** (Rule / Why / Bad / Correct). |
| **`.ai/cursor/skills/`** | Invoked workflows: rule alignment, lesson capture, feature wrap-up, commit, PR. |
| **`.ai/cursor/agents/*.md`** | **Cursor Subagents:** CI-parity Gradle/Detekt/build checks in an isolated agent context; see [`.ai/cursor/WORKFLOWS.md`](../.ai/cursor/WORKFLOWS.md). |
| **`.ai/cursor/commands/speckit.*.md`** | **Spec Kit** slash commands (Specify 0.4.0, `cursor-agent` target). |
| **`.ai/specify/`** | Spec Kit templates, constitution scaffold (`memory/constitution.md`), and helper scripts. |
| **`.gitbook/`** | Human-oriented handbook (not the agent’s primary runtime context). |
| **Serena MCP** | Symbol-aware navigation, references, and targeted edits via [`Model Context Protocol`](https://modelcontextprotocol.io/introduction); configured in [`.ai/cursor/mcp.json`](../.ai/cursor/mcp.json). Project settings live under `.ai/serena/` locally (gitignored), typically `.ai/serena/project.yml`. Memory Bank stays authoritative over Serena memories. |
| **Repomix** | Optional compact codebase snapshots (CLI or MCP); config [repomix.config.jsonc](../repomix.config.jsonc); outputs under `.repomix/` (ignored by git). |

**Precedence:** For **what code must do**, obey the closest `AGENTS.md` + `.ai/cursor/rules/`. For **what the project is doing now** and **brownfield context**, prefer `.ai/cursor/memory-bank/` and update it when wrap-up skills say so. **Serena** and **Repomix** augment retrieval—they do not replace `AGENTS.md`, `.ai/cursor/rules/`, Memory Bank, or Spec Kit artifacts.

## Serena and Repomix (optional tooling)

- **Serena:** Use for symbol lookup, references, and edits across the repo. Prefer Serena before scanning large trees with naive file reads. Project-local MCP uses `--context ide` and `--project ${workspaceFolder}` so the open workspace is the active project.
- **Repomix:** Use when you need a **single packed snapshot** (for example onboarding an external review or attaching a bounded export). Routine navigation should use Serena or normal repo tools, not Repomix.
- **Memory Bank vs Serena memories:** Canonical durable context for Headway agents remains `.ai/cursor/memory-bank/` plus `/capture-lesson` updates to `lessons-learned.mdc`. This repo’s Serena project disables memory onboarding modes so Serena does not compete with Memory Bank for policy.

## Spec Kit (brownfield specs)

Use Spec Kit when you want a structured **spec → plan → tasks → implement** loop alongside normal development.

Typical flow:

1. `/speckit.constitution` — align principles (uses `.ai/specify/memory/constitution.md` as the working constitution file).
2. `/speckit.specify` — draft or refine a feature spec from a description.
3. Optional: `/speckit.clarify` before planning if requirements are ambiguous.
4. `/speckit.plan` — implementation plan from the spec.
5. `/speckit.tasks` — actionable task breakdown.
6. `/speckit.implement` — execute against the artifacts above.

Optional quality commands (see command descriptions): `/speckit.analyze`, `/speckit.checklist`, `/speckit.taskstoissues`.

**CLI:** Specify was installed with `uvx … specify init --here --ai cursor-agent --offline` (see `.ai/specify/init-options.json`). If `specify init` hits GitHub API rate limits, use `--offline` or set `GH_TOKEN`.

**Git branch names:** In this repo, `.ai/specify/init-options.json` sets `branch_numbering` to **`headway`**: `/speckit.specify` creates a branch like `server-headway/<N>-short-name` and a matching spec folder `.ai/specs/<N>-short-name`, where `<N>` is the next free board/issue-style id (from existing `.ai/specs/<digits>-*` and matching git branches). That matches `/commit` task numbers (`SERVER-HEADWAY-<N>:` / `CLIENT-HEADWAY-<N>:` / `FULLSTACK-HEADWAY-<N>:`). To use Speckit’s original `001-feature` or timestamp branches instead, set `branch_numbering` to `sequential` or `timestamp`.

`feature_resolution` in the same file still controls validation and fallbacks:

| Field | Purpose |
|-------|---------|
| `validate_git_branch` | `false` — skip the Speckit branch-name check (still need a way to resolve `FEATURE_DIR`; see below). |
| `extra_branch_regex` | POSIX extended regex; if set and `validate_git_branch` is `true`, branches matching this **or** the default Speckit / headway patterns pass validation. |
| `fixed_specs_subdir` | Optional: basename under `.ai/specs/` when the current branch does not map to a folder (rare if you use headway or `001-…` / timestamp names). |

`check-prerequisites.sh --json --paths-only` reads the same rules. You can run with `SPECIFY_FEATURE=<branch-or-spec-folder-name>` so `FEATURE_DIR` resolves while on another git branch.

## Memory Bank maintenance

- **`/capture-lesson`:** Still appends or merges **lessons-learned.mdc** for reusable mistakes. Also evaluate whether the same insight should update **Memory Bank** (e.g. a new cross-cutting pattern in `systemPatterns.md`, or a correction in `techContext.md`). Do **not** duplicate long prose—link or summarize.
- **`/finish-feature`:** Still produces wrap-up (rules, checks, drift, optional promotion hints). Additionally: **Memory Bank drift review** — update `activeContext.md`, append to `progress.md` when the session changed workflow or context, and promote stable patterns to `systemPatterns.md` / `techContext.md` when appropriate.

## Other skills

- **`/headway-rule-aware-workflow`** — while coding, align with the closest `AGENTS.md`, `.ai/cursor/rules/`, and skim **Memory Bank** for current focus.
- **`/commit`**, **`/pull-request`** — unchanged; see [`.ai/cursor/WORKFLOWS.md`](../.ai/cursor/WORKFLOWS.md).

## Subagents

Subagents (`.ai/cursor/agents/`) are for **noisy or scoped verification** (full Gradle output, platform builds). Invoke with `/headway-detekt`, `/headway-client-android-build`, `/headway-pre-merge-verify`, etc. **Commit and PR** stay on the **`/commit`** and **`/pull-request`** Skills, not a parallel git subagent. Full list: [`.ai/cursor/WORKFLOWS.md`](../.ai/cursor/WORKFLOWS.md) → *Subagents*.

## Recommended habit

1. Open **`.ai/cursor/memory-bank/activeContext.md`** when picking up a task.
2. Edit code under the closest **`AGENTS.md`**.
3. For sizable features, run **Spec Kit** commands to generate spec artifacts under `.ai/specify/` (and feature branches as the scripts expect).
4. During exploration, when Serena MCP is enabled, prefer **symbol-aware tools** before broad reads; use **Repomix** only when a compact export is explicitly useful.
5. End with **`/finish-feature`**; run **`/capture-lesson`** after fixes that should not recur.

### Compact snapshots (Repomix CLI)

From the repo root (requires Node/npm). Official docs typically use `npx`:

```bash
npx -y repomix --output .repomix/repomix-output.xml
```

If `npx` fails to resolve the package on your machine, use npm’s exec form:

```bash
npm exec --yes --package=repomix -- repomix --output .repomix/repomix-output.xml
```

Add `--compress` for a more token-efficient structural extract when needed. Generated files under `.repomix/` are gitignored.
