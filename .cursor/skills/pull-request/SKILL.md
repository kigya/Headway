---
name: pull-request
description: >-
  Pushes the current branch, opens or updates a PR into trunk (title, body,
  labels, Closes #N for Development), optional GitHub Project via gh. Use when
  the user runs /pull-request.
disable-model-invocation: true
---

# Pull request (push + create or update)

## Prerequisites

- Read MCP tool schemas (`list_pull_requests`, `create_pull_request`, `update_pull_request`, `pull_request_read`, `issue_write`, `search_pull_requests`) before calling them.
- Branch and task parsing: same rules as [.cursor/skills/commit/SKILL.md](../commit/SKILL.md) steps 3–4 (task `<NUMBER>`, branch kind among `fullstack-headway`, `server-headway`, `client-headway`).

## Label names (repository)

Exact strings: `headway-client`, `headway-server`, `headway-fullstack`; types: `feature`, `bug`, `tech`, `research`; optional `design-system`, `draft`. Not `Headway-Client` / `Headway-Backend`.

## Instructions

1. Current branch: `git branch --show-current`. If it is `trunk`, stop unless the user explicitly asked to open a PR from `trunk`.
2. `owner` / `repo` from `git remote get-url origin` (HTTPS or SSH → parse `owner/repo`).
3. **`git push -u origin HEAD`** with git write and network permissions. On failure, report output and suggest fetch/rebase/auth.
4. **Find open PR:** `list_pull_requests` with `owner`, `repo`, `state: open`, `base: trunk`, `head: "<owner>:<current-branch>"`. If empty, **`search_pull_requests`** with query `is:pr is:open head:<branch> repo:owner/repo` (and `owner`/`repo` params if needed). Pick the PR whose head branch matches the current branch.
5. **Create vs update:** If no matching open PR → `create_pull_request` (`base: trunk`, `head` = branch name, `title` and `body` from steps 7–8, `draft` only if the user asked). If a PR exists → do **not** create another; use `update_pull_request` for `title` and `body`.
6. **Scope from diff:** `git fetch origin trunk` when needed, then `git diff origin/trunk...HEAD --name-only` (or `git merge-base origin/trunk HEAD` + diff). Classify paths: `client/`, `server/`, other. Title prefix:
   - both `client/` and `server/` in diff → `FULLSTACK-HEADWAY-<NUMBER>:`;
   - only `client/` → `CLIENT-HEADWAY-<NUMBER>:`;
   - only `server/` → `SERVER-HEADWAY-<NUMBER>:`;
   - only other paths: derive from branch kind (commit skill rule for “other”); if branch kind conflicts with diff, ask once.
7. **Title:** `<PREFIX>: <SHORT SUMMARY>` on one line (e.g. `SERVER-HEADWAY-28: Short summary`; English; no `TODO`/`WIP`).
8. **Body:** Rebuild each run: short **Summary**, bullet **Changes** inferred from diff/commits, link to board [project view](https://github.com/users/kigya/projects/4/views/2), and a line **`Closes #<NUMBER>`** (or `Fixes #<NUMBER>`) using the task number from the branch so the issue **Development** section shows this PR. **Trade-off:** when the PR merges into the repository **default branch**, GitHub will **close** that issue ([linking docs](https://docs.github.com/en/issues/tracking-your-work-with-issues/using-issues/linking-a-pull-request-to-an-issue)). If `trunk` is not the default branch, note in the reply that linking keywords may not apply until the default branch matches; suggest checking `git remote show origin`.
9. **Labels:** `pull_request_read` `method: get` for the PR number. Collect existing label **names**. Build target set: one domain label (`headway-client` / `headway-server` / `headway-fullstack` from scope); one type label from `feature` / `bug` / `tech` / `research` (prefer `tech` for internal-only refactors if unclear); add `design-system` if diff substantially touches [client/core/design-system/](../../../client/core/design-system/); add `draft` only if the user asked or the PR is draft. **Merge** target with existing labels (union), then `issue_write` `method: update`, `issue_number` = PR number, `labels` = **full merged list** (strings). If the API replaces labels, the merged list must be complete.
10. **Optional — GitHub Project (user project 4):** No Projects tool in GitHub MCP. If `gh` is available and authenticated, try `gh project` or `gh api graphql` to attach or update the issue/PR on [users/kigya/projects/4](https://github.com/users/kigya/projects/4); on failure or missing `gh`, skip and tell the user to set the project column (e.g. In Review) manually.

## Constraints

- Base branch is `trunk` unless the user names another base in the message.
- Do not push `trunk` without explicit user request (step 1).

## Output

PR URL, number, whether it was created or updated, and a one-line note if project automation was skipped.
