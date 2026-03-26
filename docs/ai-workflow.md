# AI workflow (Spec Kit + Memory Bank + Cursor)

This document describes how **AI-assisted development** is wired in Headway. It complements `AGENTS.md` (coding rules) and does not replace it.

## Where things live

| Layer | Purpose |
|-------|---------|
| **Closest `AGENTS.md`** | Canonical **code policy** by path (root, `client/`, `server/`, design-system). |
| **`.cursor/rules/*.mdc`** | Short, enforceable agent rules derived from or pointing at `AGENTS.md`. |
| **`.cursor/memory-bank/`** | Canonical **project context** for agents: orientation, current focus, stack summary, pattern reminders. |
| **`.cursor/rules/lessons-learned.mdc`** | Curated **reusable mistakes** (Rule / Why / Bad / Correct). |
| **`.cursor/skills/`** | Invoked workflows: rule alignment, lesson capture, feature wrap-up, commit, PR. |
| **`.cursor/commands/speckit.*.md`** | **Spec Kit** slash commands (Specify 0.4.0, `cursor-agent` target). |
| **`.specify/`** | Spec Kit templates, constitution scaffold (`memory/constitution.md`), and helper scripts. |
| **`.gitbook/`** | Human-oriented handbook (not the agent’s primary runtime context). |

**Precedence:** For **what code must do**, obey the closest `AGENTS.md` + `.cursor/rules/`. For **what the project is doing now** and **brownfield context**, prefer `.cursor/memory-bank/` and update it when wrap-up skills say so.

## Spec Kit (brownfield specs)

Use Spec Kit when you want a structured **spec → plan → tasks → implement** loop alongside normal development.

Typical flow:

1. `/speckit.constitution` — align principles (uses `.specify/memory/constitution.md` as the working constitution file).
2. `/speckit.specify` — draft or refine a feature spec from a description.
3. Optional: `/speckit.clarify` before planning if requirements are ambiguous.
4. `/speckit.plan` — implementation plan from the spec.
5. `/speckit.tasks` — actionable task breakdown.
6. `/speckit.implement` — execute against the artifacts above.

Optional quality commands (see command descriptions): `/speckit.analyze`, `/speckit.checklist`, `/speckit.taskstoissues`.

**CLI:** Specify was installed with `uvx … specify init --here --ai cursor-agent --offline` (see `.specify/init-options.json`). If `specify init` hits GitHub API rate limits, use `--offline` or set `GH_TOKEN`.

**Git branch names:** Default Spec Kit scripts only accept branches like `001-feature-name` or `YYYYMMDD-HHMMSS-feature-name`. Headway may use other conventions (for example `server-headway/79-training-session-flow`). Configure `.specify/init-options.json` → `feature_resolution`:

| Field | Purpose |
|-------|---------|
| `validate_git_branch` | `false` — skip the Speckit branch-name check (still need a way to resolve `FEATURE_DIR`; see below). |
| `extra_branch_regex` | POSIX extended regex; if set and `validate_git_branch` is `true`, branches matching this **or** the default Speckit patterns pass validation. |
| `fixed_specs_subdir` | Basename of the folder under `specs/` to use when the current branch is **not** `001-…` or timestamp-prefixed (required for custom branch names unless you export `SPECIFY_FEATURE` to a Speckit-style name). |

`check-prerequisites.sh --json --paths-only` reads the same rules. You can instead run with `SPECIFY_FEATURE=001-my-feature` (no init change) so `FEATURE_DIR` resolves to `specs/001-my-feature` while staying on any git branch.

## Memory Bank maintenance

- **`/capture-lesson`:** Still appends or merges **lessons-learned.mdc** for reusable mistakes. Also evaluate whether the same insight should update **Memory Bank** (e.g. a new cross-cutting pattern in `systemPatterns.md`, or a correction in `techContext.md`). Do **not** duplicate long prose—link or summarize.
- **`/finish-feature`:** Still produces wrap-up (rules, checks, drift, optional promotion hints). Additionally: **Memory Bank drift review** — update `activeContext.md`, append to `progress.md` when the session changed workflow or context, and promote stable patterns to `systemPatterns.md` / `techContext.md` when appropriate.

## Other skills

- **`/headway-rule-aware-workflow`** — while coding, align with the closest `AGENTS.md`, `.cursor/rules/`, and skim **Memory Bank** for current focus.
- **`/commit`**, **`/pull-request`** — unchanged; see [`.cursor/WORKFLOWS.md`](../.cursor/WORKFLOWS.md).

## Recommended habit

1. Open **`.cursor/memory-bank/activeContext.md`** when picking up a task.
2. Edit code under the closest **`AGENTS.md`**.
3. For sizable features, run **Spec Kit** commands to generate spec artifacts under `.specify/` (and feature branches as the scripts expect).
4. End with **`/finish-feature`**; run **`/capture-lesson`** after fixes that should not recur.
