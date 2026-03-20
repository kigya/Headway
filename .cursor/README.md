# Cursor agent guidance (Headway)

## Purpose

Rules in [`.cursor/rules/`](rules/) turn [AGENTS.md](../AGENTS.md), [client/AGENTS.md](../client/AGENTS.md), and [server/AGENTS.md](../server/AGENTS.md) into short, enforceable instructions for the AI agent. Those `AGENTS.md` files stay the canonical reference and are not replaced by this folder.

## `/capture-lesson` (conceptual workflow)

There is no command file in this repo yet; treat this as the intended flow:

1. User describes what went wrong (symptom + optional file path or PR).
2. Agent confirms the root cause and whether it is reusable beyond a one-off typo.
3. Agent appends one atomic entry to [`rules/lessons-learned.mdc`](rules/lessons-learned.mdc) using the four-field format defined there (Rule / Why / Bad / Correct).
4. Optional: add a single-line pointer in “Why” (ticket or PR link).

Goal: the same mistake should be visible on the next task without rereading a long chat.

## Hooks (future, non-destructive)

Possible extensions (not implemented here):

- After saving `*.kt` under `client/` or `server/`, remind to run the matching `detekt` task from the correct root.
- When the user signals “don’t do that again,” prompt whether to add a `lessons-learned` entry.

Keep any real hook implementation opt-in and small so it does not spam or block local work.

## CI / PR automation (future)

Practical extensions:

- Run `./gradlew detekt` (and client/server build smoke targets) on every PR touching `client/` or `server/`.
- Optional heuristic checks (e.g. flag `feature/*/internal/ui` diffs that introduce `MaterialTheme` without Freud) as review hints only—false positives are likely, so keep these non-blocking unless tuned.

Human review remains responsible for rule quality; automation should surface drift, not replace judgment.
