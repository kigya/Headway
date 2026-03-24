# Cursor agent guidance (Headway)

## Purpose

Rules in [`.cursor/rules/`](rules/) turn [AGENTS.md](../AGENTS.md), [client/AGENTS.md](../client/AGENTS.md), and [server/AGENTS.md](../server/AGENTS.md) into short, enforceable instructions for the AI agent. Those `AGENTS.md` files stay the canonical reference and are not replaced by this folder.

External dashboards and refs (Supabase, GitHub project): [RESOURCES.md](RESOURCES.md).

## Workflows (explicit invocation)

See [WORKFLOWS.md](WORKFLOWS.md) for when and how to use:

- `/headway-rule-aware-workflow` — align edits with `AGENTS.md` and `.cursor/rules` while coding.
- `/capture-lesson` — record a **reusable** mistake in [`rules/lessons-learned.mdc`](rules/lessons-learned.mdc) after a fix (merge or skip as appropriate).
- `/finish-feature` — wrap-up: critical rules, verification commands, drift risks, optional promotion hints.
- `/commit` — `git add -A` (or given paths), then `git commit` with `CLIENT-HEADWAY-` / `SERVER-HEADWAY-` / `FULLSTACK-HEADWAY-` + task number from the branch; no push unless you ask.
- `/pull-request` — push branch, create or update PR into `trunk` (title, body, labels, `Closes #N`); optional `gh` for GitHub Project.

Skills live under [`.cursor/skills/`](skills/). There are no hooks, watchers, or CI steps in this layer.
