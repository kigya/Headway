---
icon: diagram-project
---

# Developing a feature (Spec Kit + PRD)

Headway uses **Spec Kit** for structured, spec-driven development, with the **PRD** as the product source of truth and `AGENTS.md` as the code source of truth. This page is the recommended end-to-end flow and which command to call for what. It works identically from **Cursor** and **Codex** (both read the shared agent context under `.ai/`).

## The mental model

| Artifact | Lives in | Role |
|----------|----------|------|
| **PRD** | `.gitbook/product/prd/<screen>.md` | *What* the app should do and *why* (product contract) |
| **Code policy** | closest `AGENTS.md` + `.ai/cursor/rules/` | *How* code must be written |
| **Spec / Plan / Tasks** | `.ai/specs/<feature>/` | The *working plan* for one change |
| **Current context** | `.ai/cursor/memory-bank/` | What we're focused on right now |
| **Constitution** | `.ai/specify/memory/constitution.md` | Normative rules Spec Kit must honor |

The golden rule: **PRD ↔ code stay in sync.** A feature starts from a PRD (or writes one) and ends by updating that PRD to match what shipped.

## Step-by-step

### 0. Orient (once per task)

* Skim `.ai/cursor/memory-bank/activeContext.md` for current focus.
* Open the **PRD** for the screen(s) you're touching under [`product/prd/`](../product/prd/README.md). If the screen has no PRD yet, write/extend it first (or ask the product owner the open questions).

### 1. `/speckit.constitution` *(rarely; only to align principles)*

Refreshes/affirms the engineering constitution. Run when project principles change. Most features skip this.

### 2. `/speckit.specify` — draft the feature spec

Describe the feature in plain language. Spec Kit creates a branch like `…-headway/<N>-short-name` and a folder `.ai/specs/<N>-short-name/` with `spec.md`. **Link the relevant PRD** in the spec (the spec template has a *Related PRD* field) and keep requirements consistent with it.

### 3. `/speckit.clarify` *(optional, before planning)*

Resolves ambiguities in the spec via targeted questions. Use it whenever the PRD has **open questions** or the spec has `[NEEDS CLARIFICATION]`. Answers should be reflected back into the PRD.

### 4. `/speckit.plan` — implementation plan

Turns the spec into a technical plan (modules, files, data, contracts) that must respect the closest `AGENTS.md` and the constitution.

### 5. `/speckit.tasks` — task breakdown

Produces an actionable, ordered task list (`tasks.md`). Optionally `/speckit.taskstoissues` to push them to the board, and `/speckit.checklist` / `/speckit.analyze` for quality gates.

### 6. `/speckit.implement` — build it

Executes the tasks. While implementing, the `prd-sync` rule and `/headway-rule-aware-workflow` keep edits aligned with the PRD, `AGENTS.md`, rules, and Memory Bank. If the code needs to deviate from the PRD, **ask the product owner** and record the decision in the PRD.

### 7. Verify

* **Client:** `cd client && ./gradlew app:headwayAndroid:assembleDebug` and `cd client && ./gradlew detekt`.
* **Server:** `cd server && ./gradlew build` and `cd server && ./gradlew detekt`.

### 8. `/finish-feature` — wrap up

Reviews critical rules, expected checks, drift, and **Memory Bank** updates. It also includes a **PRD drift review**: update the screen PRD's *Status*, requirements, technical mapping, and open questions to match what shipped. Use `/capture-lesson` after fixing a reusable mistake.

### 9. Commit / PR

`/commit` and `/pull-request` (see [WORKFLOWS](https://github.com/kigya/Headway/blob/trunk/.ai/cursor/WORKFLOWS.md)). Task numbers follow `CLIENT-HEADWAY-<N>` / `SERVER-HEADWAY-<N>` / `FULLSTACK-HEADWAY-<N>`.

## Quick reference

```text
orient → PRD → /speckit.specify → [/speckit.clarify] → /speckit.plan
       → /speckit.tasks → /speckit.implement → verify → /finish-feature (updates PRD) → /commit → /pull-request
```

## Tooling that keeps context consistent

* **PRD (GitBook)** — product contract; agents read it before screen work and update it after.
* **Serena MCP** — symbol-aware navigation/edits; prefer it over broad file scans.
* **Memory Bank** — current focus and durable patterns; not a substitute for `AGENTS.md`.
* **Repomix** — only for explicit compact snapshots.

See [`.ai/docs/ai-workflow.md`](https://github.com/kigya/Headway/blob/trunk/.ai/docs/ai-workflow.md) for the full wiring (Cursor + Codex parity, precedence, and the PRD maintenance loop).
