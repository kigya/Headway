# Cursor agent guidance (Headway)

## Purpose

Rules in [`.ai/cursor/rules/`](rules/) turn [AGENTS.md](../AGENTS.md), [client/AGENTS.md](../client/AGENTS.md), and [server/AGENTS.md](../server/AGENTS.md) into short, enforceable instructions for the AI agent. Those `AGENTS.md` files stay the canonical reference for **code policy** and are not replaced by this folder.

**Project context** for agents (orientation, current focus, stack recap) lives in [`.ai/cursor/memory-bank/`](memory-bank/). Full layering is described in [.ai/docs/ai-workflow.md](../docs/ai-workflow.md).

External dashboards and refs (Supabase, GitHub project): [RESOURCES.md](RESOURCES.md).

## Spec Kit (slash commands)

Command definitions: [`.ai/cursor/commands/`](commands/) (`speckit.*`). Templates and scripts: [`.ai/specify/`](../specify/) in the repo root.

## MCP (Serena + Repomix)

Project-local MCP servers live in [`.ai/cursor/mcp.json`](mcp.json). **Serena** (`ide` context, workspace project) handles symbolic navigation and edits; **Repomix** runs `npx repomix --mcp` for packed snapshots. Roles and CLI hints: [.ai/docs/ai-workflow.md](../docs/ai-workflow.md) → *Serena and Repomix*. Reload Cursor after changing `mcp.json` if servers do not appear.

## Workflows (explicit invocation)

See [WORKFLOWS.md](WORKFLOWS.md) for when and how to use:

- `/headway-rule-aware-workflow` — align edits with `AGENTS.md`, `.ai/cursor/rules`, and Memory Bank context while coding.
- `/capture-lesson` — record a **reusable** mistake in [`rules/lessons-learned.mdc`](rules/lessons-learned.mdc) after a fix (merge or skip as appropriate); update Memory Bank when the lesson changes ongoing context.
- `/finish-feature` — wrap-up: critical rules, verification commands, drift risks, Memory Bank drift review, optional promotion hints.
- `/commit` — `git add -A` (or given paths), then `git commit` with `CLIENT-HEADWAY-` / `SERVER-HEADWAY-` / `FULLSTACK-HEADWAY-` + task number from the branch; no push unless you ask.
- `/pull-request` — push branch, create or update PR into `trunk` (title, body, labels, `Closes #N`); optional `gh` for GitHub Project.

Skills live under [`.ai/cursor/skills/`](skills/). There are no hooks, watchers, or CI steps in this layer.
