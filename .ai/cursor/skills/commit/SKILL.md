---
name: commit
description: >-
  Stages local working-tree changes and runs git commit with Headway message
  convention (CLIENT-HEADWAY-/SERVER-HEADWAY-/FULLSTACK-HEADWAY- plus board task
  number). Does not push. Use when the user runs /commit.
disable-model-invocation: true
---

# Commit (local changes + git commit)

## Instructions

1. Repository root: run from the git root of Headway (where `.git` lives).
2. `git status --porcelain`. If there is nothing to commit (empty output), stop and say the working tree is clean.
3. **Paths in scope:** If the user listed specific paths in the same message, use only those paths for the checks below and for `git add`. Otherwise use **all** paths that appear in the porcelain output (modified, added, deleted, untracked that Git lists — ignored paths do not appear). For renames, use the final path.
4. Read the current branch with `git branch --show-current`.
5. **Task number** from the branch name. Match the first occurrence after any of:
   - `client-headway/`, `client-headway-`
   - `server-headway/`, `server-headway-`
   - `fullstack-headway/`, `fullstack-headway-`
   Optional leading `feature/` is allowed; `/` or `-` may follow the keyword. Capture the **first digit sequence** as `<NUMBER>`. Examples: `feature/client-headway-8-nav` → `8`; `client-headway/16-readme` → `16`. If no match, stop and ask the user for `<NUMBER>` (and for prefix if the branch is ambiguous).
6. **Branch kind** from the branch name (first matching segment among `fullstack-headway`, `server-headway`, `client-headway` in path order after stripping optional `feature/`).
7. **Path scope** from the paths in step 3:
   - any under `client/` (prefix `client/`) → client-touched;
   - any under `server/` → server-touched;
   - neither → **other** (e.g. `.ai/cursor/`, root docs, `build-logic/`).
8. **Message prefix** (uppercase, hyphenated as below):
   - If both client-touched and server-touched → `FULLSTACK-HEADWAY-<NUMBER>`.
   - If only client-touched → `CLIENT-HEADWAY-<NUMBER>`.
   - If only server-touched → `SERVER-HEADWAY-<NUMBER>`.
   - If only **other** paths: derive prefix from branch kind (`client-headway` → CLIENT, `server-headway` → SERVER, `fullstack-headway` → FULLSTACK). If branch kind is unclear, ask the user which prefix to use.
9. **Branch vs path mismatch:** If branch kind is `server-headway` but changes are only client-touched (or the opposite), warn and ask for confirmation **before** staging; if the user does not confirm, stop without `git add`.
10. **Stage:** `git add -A` at repo root, or `git add` on the user-supplied paths only. Then `git diff --cached --name-only`; if empty, stop.
11. **Summary:** One line in English after the colon, concise, describing the staged change. Infer from `git diff --cached`. No `TODO`, `WIP`, or placeholder wording.
12. **Final message:** `<PREFIX>: <SHORT SUMMARY>` on a single line (e.g. `SERVER-HEADWAY-8: Add healthcheck endpoint`).
13. Run `git commit -m "<message>"` with git write permission. Do not pass `--no-verify` unless the user explicitly requests it. Do **not** run `git push` unless the user explicitly asks in the same message.

## Constraints

- One logical commit per `/commit` invocation; default is all non-ignored local changes (`git add -A`), unless the user named paths.
- Follow shared Kotlin/repo hygiene from [AGENTS.md](../../../../AGENTS.md) for message quality.

## Output

Reply with the exact commit message used and the short hash from the successful commit (or the reason you stopped).
