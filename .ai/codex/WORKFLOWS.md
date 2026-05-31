# Headway Codex workflows

This file maps the existing Headway AI workflows to Codex. The shared workflow
sources live under `skills/`, `commands/`, and `agents/` through symlinks to
`.ai/cursor/`.

## Rule-aware coding

For substantive Kotlin work, use `skills/headway-rule-aware-workflow/SKILL.md`
as the project-specific coding workflow:

- read the closest `AGENTS.md`;
- skim `memory-bank/activeContext.md` and `memory-bank/systemPatterns.md`;
- load `rules/global.mdc`, `rules/lessons-learned.mdc`, and the matching
  client/server/detekt rules;
- prefer existing Headway patterns over new abstractions.

## Feature wrap-up

Use `skills/finish-feature/SKILL.md` before merge or PR-style handoff. In Codex,
that means:

- summarize the critical rules that applied;
- run or report the expected Gradle and Detekt checks by scope;
- update Memory Bank only when durable context actually changed;
- suggest `AGENTS.md` promotion candidates only when the lessons file is bloated.

## Lessons

Use `skills/capture-lesson/SKILL.md` when the user asks to record a correction
or after fixing a reusable mistake. Keep one-off typos and task-specific copy
out of `rules/lessons-learned.mdc`.

## Git flows

Use `skills/commit/SKILL.md` and `skills/pull-request/SKILL.md` as the local
conventions for commit messages, PR titles, labels, and Headway task numbers.
Codex still follows its own safety rules for staging, committing, pushing, and
PR creation.

## Spec Kit

The `commands/speckit.*.md` files are command recipes generated for the
Cursor-agent target. Codex can still read them and run the helper scripts in
`specify/` directly when executing the same spec -> plan -> tasks -> implement
flow. Existing brownfield feature artifacts are available through `specs/`.

## Verification recipes

Files in `agents/` are Cursor Subagent definitions. In Codex, treat them as
bounded verification recipes:

- run the listed commands directly when appropriate; or
- use Codex subagents only when the user explicitly asks for delegated or
  parallel agent work.

## MCP

`mcp.json` mirrors the Cursor MCP setup for Serena and Repomix. If Codex has the
same MCP tools available, prefer Serena for symbol-aware navigation and targeted
edits, and use Repomix only for deliberate compact snapshots.
