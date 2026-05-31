# Progress

Changelog for **AI workflow** and **Memory Bank** maintenance (not product release notes).

## 2026-05-30

- **GitBook PRD:** Added `Product` section to `.gitbook/` — `product/prd/README.md` (overview, screen inventory, conventions), `product/methodology.md` (interview-prep domain), and per-screen PRDs for Splash, Loading indicator, Error & network stubs, Auth, No access (with screenshots under `product/prd/assets/`). Updated `.gitbook/SUMMARY.md` and added `general/feature-workflow.md` (Spec Kit + PRD flow).
- **Autonomy wiring:** New `.ai/cursor/rules/prd-sync.mdc` (read PRD before screen/data-path work, ask product owner on ambiguity, update PRD after). Extended `.ai/docs/ai-workflow.md` (PRD layer + precedence + sync loop), `/headway-rule-aware-workflow` and `/finish-feature` skills (PRD steps), Spec Kit `spec-template.md` (Related PRD field) and `constitution.md` (Principle I product source-of-truth). Codex inherits all via `.ai/codex` symlinks.
- **Note:** Supabase MCP was unreachable this session; PRD `Data & entities` were derived from Exposed table definitions in `server/database/internal` (source of truth in code).

## 2026-04-29

- **Cursor:** Added project MCP config (Serena `ide` context + Repomix stdio), local `.ai/serena/project.yml` with symbolic languages and Memory-Bank-first prompts, `repomix.config.jsonc`, `.gitignore` for `.repomix/` outputs and `.ai/serena/`.
- **Docs:** `.ai/docs/ai-workflow.md`, `.ai/cursor/README.md`, `.ai/cursor/WORKFLOWS.md`, `headway-rule-aware-workflow` skill, `.ai/cursor/rules/global.mdc`, Memory Bank (`techContext.md`, `systemPatterns.md`, `activeContext.md`) updated for layering.

## 2026-03-27

- **Server (spec 003):** learning-questions HTTP + GraphQL surface (database internal + gateway), guest session register/revoke/validate, preparation→learning progress sync; `@SerialName` coverage on affected wire models and Ktor resources; non-repository persistence helpers moved to `data/scope` and `data/learning`.
- **Cursor:** `lessons-learned.mdc` §18–§19; `systemPatterns.md` updated for serialization and `data/` layout.

## 2026-03-24

- Added GitHub **Spec Kit** artifacts: `.ai/specify/` (templates, memory constitution scaffold, bash + PowerShell scripts), `.ai/cursor/commands/speckit.*.md`.
- Introduced **Memory Bank** under `.ai/cursor/memory-bank/` as canonical persistent **project context** for agents.
- Adjusted `.gitignore` to ignore `.ai/cursor/projects/` instead of blanket `.ai/cursor/*`.
- Documented layering in `.ai/docs/ai-workflow.md`; wired `/capture-lesson` and `/finish-feature` skills to consider Memory Bank updates.
