# Product context

- **What Headway is:** the in-app replacement for Innowise's Google-Sheet interview-preparation process. Mentors/managers run structured preparation sessions for invited employees against a curated question bank; employees track progress until interview-ready. Built with Kotlin Multiplatform + Compose for Android, iOS, Desktop, and Web (a deliberate KMP proof-of-concept). Serves the Android sub-department today; designed to scale to iOS and Cross-platform.
- **Product source of truth (PRD):** `.gitbook/product/prd/` — one spec per screen (purpose, states, FR/NFR, Supabase entities, code mapping) plus the screen inventory in `prd/README.md`. Domain background (Fast/Growth/Deep tracks; Check/Slice/Mock sessions): `.gitbook/product/methodology.md`.
- **Sync loop:** screen/data-path work reads the matching PRD first and updates it after (status, requirements, data, code mapping, open questions); ambiguity goes to the product owner. Enforced by `.ai/cursor/rules/prd-sync.mdc`, `/headway-rule-aware-workflow`, and `/finish-feature`. See `.ai/docs/ai-workflow.md`.
- Public docs/handbook live in **GitBook**; editable sources under `.gitbook/`. Quickstart lists the monorepo split: `client/` (KMP app), `server/` (Ktor services), shared Kotlin policy in `AGENTS.md` files.

## Open questions

- Roadmap and release naming are not pinned here; confirm with the product owner / issue tracker when it matters.
- Per-screen open questions live in each PRD's "Open questions & planned work" section (e.g. Auth allowlist tightening to innowise.com only; No access "Ask manager" deep-link/clipboard behavior + copy).
