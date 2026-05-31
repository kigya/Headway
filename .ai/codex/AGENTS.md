# AGENTS.md - Codex AI Layer

This directory is a Codex-facing adapter for shared Headway AI context, not a
separate source of project policy.

## Do

- Keep the repo root `.codex` symlink pointed at `.ai/codex`.
- Keep shared context folders as symlinks to `.ai/cursor` unless the team
  intentionally creates a new shared layer.
- Use this directory for Codex-specific orientation and workflow mapping.
- Edit shared rules, skills, and Memory Bank through their canonical shared
  targets so Cursor and Codex see the same context.

## Don't

- Do not duplicate Memory Bank, rules, skills, commands, or agents into a
  second Codex-only copy.
- Do not remove or repoint `.cursor`; Cursor workflows must keep working.
- Do not store secrets, local logs, or generated runtime state in committed
  Codex docs.
