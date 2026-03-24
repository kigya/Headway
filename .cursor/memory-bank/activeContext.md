# Active context

## Current branch intent

- `chore/spec-kit-memory-bank` — integrate **GitHub Spec Kit** (Specify 0.4.0) and a **Memory Bank** under `.cursor/memory-bank/`, without changing application or CI code.

## Immediate next steps (for the human)

- Review `.gitignore` change (`.cursor/projects/` only; `.cursor/commands/` and `.cursor/memory-bank/` are tracked).
- Skim [docs/ai-workflow.md](../../docs/ai-workflow.md) and run a Spec Kit command when starting the next spec (e.g. `/speckit.constitution` or `/speckit.specify`).
- After merge: delete stale local-only paths under `.cursor/` if your machine still has old ignored clutter.

## Notes

- `specify init --offline` avoids GitHub API rate limits; use `GH_TOKEN` if you need online template refresh later.
