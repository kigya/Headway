---
icon: clipboard-list
---

# Product requirements (PRD)

This section is the **product and technical specification** for Headway, organized **screen by screen**. Each PRD answers, in one place:

* **What** the screen does and **why** it exists (product value, in plain language).
* **How** the user experiences it (states, copy, adaptive layouts for mobile vs. wide screens).
* **Which requirements** apply (functional and non-functional), and known constraints.
* **Which data** is involved (Supabase tables / columns) and **how it is implemented** in code (modules, key files, MVI store, navigation, design-system components).

PRDs complement, and do not replace, the engineering rules:

| Layer | Source of truth | Question it answers |
|-------|-----------------|---------------------|
| **Product policy** | this PRD section | *What should the app do, and why?* |
| **Code policy** | closest `AGENTS.md` (root / `client/` / `server/` / design-system) + `.ai/cursor/rules/` | *How must the code be written?* |
| **Current context** | `.ai/cursor/memory-bank/` | *What are we working on right now?* |
| **Working artifacts** | Spec Kit specs under `.ai/specs/<feature>/` | *How is this specific change planned and tracked?* |

> If a PRD and an `AGENTS.md` ever seem to conflict, they are answering different questions. Product behavior follows the PRD; code shape follows `AGENTS.md`.

## Product vision

Headway is the in-app replacement for Innowise's Google-Sheet-based **interview preparation** process. Mentors and managers run structured preparation sessions for their employees against a curated question bank; employees track progress until they are ready for a real technical interview. The methodology these sessions follow is documented in [Interview preparation methodology](../methodology.md).

The app is built with **Kotlin Multiplatform + Compose Multiplatform** targeting **Android, iOS, Desktop, and Web** from one codebase. This is a deliberate proof-of-concept that KMP can deliver every target. The program currently serves the **Android** sub-department and is designed to scale to **iOS** and **Cross-platform** so the whole mobile department shares one preparation method.

Because the app targets phones and large screens alike, designs ship in **two layouts**: a narrow (mobile) layout and a **wide** layout for Web and Desktop. Screens choose between them at runtime via the design-system width helpers.

## Screen inventory

Status legend: ✅ implemented · 🟡 partially implemented · ⬜ planned (PRD written ahead of code).

| Screen | Status | Platforms | PRD |
|--------|--------|-----------|-----|
| Splash | 🟡 (visual final; session/prefetch to grow) | All | [splash.md](splash.md) |
| Loading indicator | ⬜ (used inline today; DS component pending) | All | [loading-indicator.md](loading-indicator.md) |
| Error & network stubs | ✅ | All | [error-network-stubs.md](error-network-stubs.md) |
| Auth (sign in / guest) | ✅ | All | [auth.md](auth.md) |
| No access | 🟡 (visual final; "Ask manager" action pending) | All | [no-access.md](no-access.md) |
| Home | 🟡 | All | _to be specified_ |
| Learn questions (guest) | 🟡 | All | _to be specified_ |

Rows marked _to be specified_ already exist in code but have not yet been walked through with the product owner; their PRDs are added as we cover each screen.

## How to read a PRD page

Every screen page follows the same template:

1. **At a glance** — status, platforms, entry points, the one-line purpose.
2. **Product value** — why the screen exists, written for a human.
3. **Experience & states** — what the user sees, including a detailed visual description of each design and the mobile-vs-wide differences.
4. **Functional requirements** (`FR-*`) and **Non-functional requirements** (`NFR-*`).
5. **Data & entities** — Supabase tables/columns touched (or "none").
6. **Technical implementation** — modules, key files, MVI/navigation/design-system mapping.
7. **Open questions & planned work** — what is intentionally not built yet.

## Keeping PRDs in sync (for agents and humans)

PRDs are **living documents**. They are written ahead of, and updated alongside, the code:

* When a feature touches a screen, the implementing agent **reads the matching PRD first**, treats it as the product contract, and **updates the PRD** (status, requirements, technical mapping, open questions) as part of the same change.
* When behavior is ambiguous or the code would contradict the PRD, the agent **asks the product owner** instead of guessing, then records the decision here.

This loop is wired into the agent toolchain so it happens without a reminder — see [`.ai/docs/ai-workflow.md`](https://github.com/kigya/Headway/blob/trunk/.ai/docs/ai-workflow.md), the `prd-sync` rule in `.ai/cursor/rules/`, and the `/finish-feature` skill. New features are specified through Spec Kit; see [Developing a feature](../../general/feature-workflow.md).

## Glossary

* **Registered user** — an invited Innowise employee signed in with Google; gets the full experience (home, progress, sessions with mentor/manager).
* **Guest** — anyone without an invite who chooses "Learn As A Guest"; read-only access to the question bank for one technology, no progress tracking or sessions.
* **Allowlist** — the set of email domains permitted to sign in (enforced in Postgres). Today `innowise.com` (target) and `gmail.com` (testing) are allowed.
* **Invite** — a user must be added to the system by a developer, manager, or mentor before they can sign in as a registered user.
* **Track / Approach** — preparation concepts (Fast/Growth/Deep tracks; Check/Slice/Mock sessions). See [methodology](../methodology.md).
