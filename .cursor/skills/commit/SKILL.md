---
name: commit
description: >-
  Produces a one-line commit message in Headway convention
  (CLIENT-HEADWAY-/SERVER-HEADWAY-/FULLSTACK-HEADWAY- plus board task number)
  from the current branch and staged diff. Does not run git commit or push.
  Use when the user runs /commit.
disable-model-invocation: true
---

# Commit message (no git write)

## Instructions

1. Run `git diff --cached --stat` and `git diff --cached --name-only`. If there is nothing staged, stop and tell the user to stage files first (or say you can only infer scope from the index).
2. Read the current branch with `git branch --show-current`.
3. **Task number** from the branch name. Match the first occurrence after any of:
   - `client-headway/`, `client-headway-`
   - `server-headway/`, `server-headway-`
   - `fullstack-headway/`, `fullstack-headway-`
   Optional leading `feature/` is allowed; `/` or `-` may follow the keyword. Capture the **first digit sequence** as `<NUMBER>`. Examples: `feature/client-headway-8-nav` → `8`; `client-headway/16-readme` → `16`. If no match, stop and ask the user for `<NUMBER>` (and for prefix if the branch is ambiguous).
4. **Branch kind** from the branch name (first matching segment among `fullstack-headway`, `server-headway`, `client-headway` in path order after stripping optional `feature/`).
5. **Staged path scope:** From staged paths only, classify:
   - has under `client/` (prefix `client/`) → client-touched;
   - has under `server/` → server-touched;
   - neither → **other** (e.g. `.cursor/`, root docs, `build-logic/`).
6. **Message prefix** (uppercase, hyphenated as below):
   - If staged includes both client-touched and server-touched → `FULLSTACK-HEADWAY-<NUMBER>`.
   - If only client-touched → `CLIENT-HEADWAY-<NUMBER>`.
   - If only server-touched → `SERVER-HEADWAY-<NUMBER>`.
   - If only **other** paths: derive prefix from branch kind (`client-headway` → CLIENT, `server-headway` → SERVER, `fullstack-headway` → FULLSTACK). If branch kind is unclear, ask the user which prefix to use.
7. **Branch vs staged mismatch:** If branch kind is `server-headway` but staged is only client-touched (or the opposite), warn and ask for confirmation before giving the final line; if the user does not confirm, stop.
8. **Summary:** One line in English after the colon, concise, describing the staged change. Infer from `git diff --cached`. No `TODO`, `WIP`, or placeholder wording.
9. **Final message:** `<PREFIX>: <SHORT SUMMARY>` on a single line (e.g. `SERVER-HEADWAY-8: Add healthcheck endpoint`).

Do **not** run `git commit`, `git push`, or `git add` unless the user explicitly asks for something else in the same message.

## Constraints

- Staged paths only for scope and summary; do not assume unstaged changes belong in the message.
- Follow shared Kotlin/repo hygiene from [AGENTS.md](../../../AGENTS.md) for message quality.

## Output

Reply with the exact one-line commit message only (copy-paste ready), or the reason you stopped.
