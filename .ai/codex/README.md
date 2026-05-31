# Codex agent guidance (Headway)

## Purpose

This directory is the Codex-facing entrypoint for Headway AI context. The repo
root `.codex` symlink points here, mirroring the existing
`.cursor -> .ai/cursor` setup.

Cursor remains supported. Shared project context still lives in the existing
Cursor-origin tree, and Codex reads the same material through symlinks here:

| Codex path | Shared target | Role |
|------------|---------------|------|
| `memory-bank/` | `.ai/cursor/memory-bank/` | Durable project context and current focus |
| `rules/` | `.ai/cursor/rules/` | Enforceable agent rules and lessons learned |
| `skills/` | `.ai/cursor/skills/` | Project workflows such as finish-feature, commit, PR |
| `commands/` | `.ai/cursor/commands/` | Spec Kit command recipes |
| `agents/` | `.ai/cursor/agents/` | Verification recipes originally written as Cursor subagents |
| `docs/` | `.ai/docs/` | Shared AI workflow documentation |
| `specify/` | `.ai/specify/` | Spec Kit templates, scripts, and constitution |
| `specs/` | `.ai/specs/` | Brownfield feature specs and task artifacts |
| `serena/` | `.ai/serena/` | Project-local Serena configuration and ignored runtime data |
| `mcp.json` | `.ai/cursor/mcp.json` | Serena and Repomix MCP configuration |
| `RESOURCES.md` | `.ai/cursor/RESOURCES.md` | Public dashboards and external links |

The symlink approach avoids a second, drifting copy of rules or memory.

## Source of Truth

- Code policy: the closest `AGENTS.md` to the file being edited.
- Project context: `memory-bank/`, especially `activeContext.md`,
  `systemPatterns.md`, and `techContext.md` for non-trivial work.
- Short agent rules: `rules/global.mdc`, the matching `client.mdc` or
  `server.mdc`, `detekt-guardrails.mdc` for Kotlin, and
  `rules/lessons-learned.mdc`.
- Spec-driven work: `commands/speckit.*.md` plus `specify/` and `specs/`.
- Full workflow model: `docs/ai-workflow.md`.
- External links: `RESOURCES.md`. Do not store secrets there.

## Codex Startup Checklist

1. Read the closest `AGENTS.md` for the paths in scope.
2. For non-trivial work, skim `memory-bank/activeContext.md` and the relevant
   stable context file (`systemPatterns.md` or `techContext.md`).
3. Load the matching rules from `rules/` and check `lessons-learned.mdc` for
   recurring mistakes that match the task.
4. Treat `skills/*/SKILL.md` as project workflow instructions. Some wording is
   Cursor-specific because the shared source remains `.ai/cursor`; translate
   slash-command references into normal Codex actions.
5. Prefer Serena or symbol-aware tools when available. Otherwise use fast local
   tools such as `rg` before broad reads.

## Maintenance

- Keep `.cursor` pointing at `.ai/cursor`; do not break Cursor workflows.
- Keep `.codex` pointing at `.ai/codex`.
- Do not duplicate Memory Bank, rules, or skills into Codex-specific copies
  unless the team intentionally creates a new shared layer.
- If a workflow changes for all agents, update the shared target under
  `.ai/cursor/`. If only Codex behavior changes, update this directory's docs.
