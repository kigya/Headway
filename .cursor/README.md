# Cursor agent guidance (Headway)

## Purpose

Rules in [`.cursor/rules/`](rules/) turn [AGENTS.md](../AGENTS.md), [client/AGENTS.md](../client/AGENTS.md), and [server/AGENTS.md](../server/AGENTS.md) into short, enforceable instructions for the AI agent. Those `AGENTS.md` files stay the canonical reference for **code policy** and are not replaced by this folder.

**Project context** for agents (orientation, current focus, stack recap) lives in [`.cursor/memory-bank/`](memory-bank/). Full layering is described in [docs/ai-workflow.md](../docs/ai-workflow.md).

External dashboards and refs (Supabase, GitHub project): [RESOURCES.md](RESOURCES.md).

## Spec Kit (slash commands)

Command definitions: [`.cursor/commands/`](commands/) (`speckit.*`). Templates and scripts: [`.specify/`](../.specify/) in the repo root.

## Workflows (explicit invocation)

See [WORKFLOWS.md](WORKFLOWS.md) for when and how to use:

- `/headway-rule-aware-workflow` — align edits with `AGENTS.md`, `.cursor/rules`, and Memory Bank context while coding.
- `/capture-lesson` — record a **reusable** mistake in [`rules/lessons-learned.mdc`](rules/lessons-learned.mdc) after a fix (merge or skip as appropriate); update Memory Bank when the lesson changes ongoing context.
- `/finish-feature` — wrap-up: critical rules, verification commands, drift risks, Memory Bank drift review, optional promotion hints.
- `/commit` — `git add -A` (or given paths), then `git commit` with `CLIENT-HEADWAY-` / `SERVER-HEADWAY-` / `FULLSTACK-HEADWAY-` + task number from the branch; no push unless you ask.
- `/pull-request` — push branch, create or update PR into `trunk` (title, body, labels, `Closes #N`); optional `gh` for GitHub Project.

Skills live under [`.cursor/skills/`](skills/). There are no hooks, watchers, or CI steps in this layer.
