# Progress

Changelog for **AI workflow** and **Memory Bank** maintenance (not product release notes).

## 2026-03-24

- Added GitHub **Spec Kit** artifacts: `.specify/` (templates, memory constitution scaffold, bash + PowerShell scripts), `.cursor/commands/speckit.*.md`.
- Introduced **Memory Bank** under `.cursor/memory-bank/` as canonical persistent **project context** for agents.
- Adjusted `.gitignore` to ignore `.cursor/projects/` instead of blanket `.cursor/*`.
- Documented layering in `docs/ai-workflow.md`; wired `/capture-lesson` and `/finish-feature` skills to consider Memory Bank updates.
