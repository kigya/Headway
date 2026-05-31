---
icon: code-branch
---

# Git, PRs, labels, and task board

## Branching model: trunk-based

* Main branch: **`trunk`**.
* Features and fixes live in short-lived branches off `trunk`.
* Land changes in **`trunk`** via **Pull Request**.

Recommendations: small branches and PRs, frequent merges, avoid long-lived “month-long” branches.

## Commit messages

The task number comes from the **project board** (do not confuse it with the GitHub PR number).

* Client: `CLIENT-HEADWAY-<NUMBER>: short description`
* Server: `SERVER-HEADWAY-<NUMBER>: short description`
* Both sides in one task: `FULLSTACK-HEADWAY-<NUMBER>: short description`

Use `FULLSTACK` only when a single task truly touches both client and server in the same commit.

Examples:

* `CLIENT-HEADWAY-16: fix README`
* `SERVER-HEADWAY-8: healthcheck`
* `FULLSTACK-HEADWAY-32: environment switcher`

## Branch names

* Client: `client-headway/<NUMBER>-short-kebab-case`
* Server: `server-headway/<NUMBER>-short-kebab-case`
* Fullstack: `fullstack-headway/<NUMBER>-short-kebab-case`

Examples: `client-headway/16-readme-docs`, `server-headway/8-healthcheck`.

## Pull request titles

Same format as commits (prefix + task number + short description). Do **not** use the GitHub PR number (`#17`) to identify the task — use the board number.

## Task board

**Link:** [Headway project — view 2](https://github.com/users/kigya/projects/4/views/2)

### Status automation (Actions)

The repo includes workflow [`.github/workflows/project-board-sync.yml`](../../.github/workflows/project-board-sync.yml):

* A PR into **`trunk`** that is not a draft (**opened** / **reopened** / **ready_for_review**) → linked issues on [user project #4](https://github.com/users/kigya/projects/4) get **Status** **In Review** (if the card is already on that project).
* After **merge** into **`trunk`** → **Status** → **Done** for the same issues.

Linking to issues works via:

* branch names following `client-headway/<NUMBER>-…`, `server-headway/<NUMBER>-…`, `fullstack-headway/<NUMBER>-…`;
* and/or `Closes #N` / `Fixes #N` / `Resolves #N` in the PR title or body (case-insensitive).

**Repository secret:** `HEADWAY_PROJECT_V2_WRITE_TOKEN` — a PAT for the project owner with permission to read and update **Projects** (for a user project, the `kigya` user token). Without the secret, the job fails. PRs from **forks** do not run the workflow (forks cannot use repo secrets).

You can still enable built-in **Workflows** on the GitHub project UI (e.g. closed issue → **Done**); they complement Actions and are configured only in the project UI.

### Columns and meaning

| Column | When to use | Notes |
|--------|-------------|-------|
| **To Do** | Task is filed, work not started | Clarify scope and dependencies before starting. |
| **In Progress** | You own the task and are actively working | One person — one main in-progress task; branch name matches the number. |
| **In Review** | PR is open, waiting for review or review fixes | Link the task in the PR description; add `Closes #N` to the issue when needed. |
| **Blocked** | Cannot proceed without a decision, access, or another task | Briefly note the blocker on the card or PR. |
| **Done** | Work is finished (often after merge to `trunk`) | Confirm acceptance criteria and any deploy/doc follow-ups. |

### Day-to-day workflow

1. Task appears in **To Do** (or you create it per team agreement).
2. Before coding: move to **In Progress**, create a branch per convention, commit with the right prefix.
3. After opening a PR: card in **In Review**; reviewers use code and the board.
4. If waiting on an external decision — **Blocked** with a comment.
5. After merge (and post-deploy if needed) — **Done**.

## GitHub labels

### Required domain label

* Client only: **`Headway-Client`**
* Server only: **`Headway-Backend`**
* Both sides: **both** labels — `Headway-Client` and `Headway-Backend`

### Optional type labels (as needed)

* `feature` — new functionality
* `bug` — defect fix
* `tech` — refactor, infrastructure, tech debt without a product feature
* `research` — spike or investigation
* `draft` — draft PR, review not expected

Example combinations:

* Client feature: `Headway-Client` + `feature`
* Server bug: `Headway-Backend` + `bug`
* Fullstack: `Headway-Client` + `Headway-Backend` + `feature`

## Checklist before opening a PR

* Branch name follows convention.
* Commits and PR title use the correct prefix and task number.
* Required labels (domain + type).
* Board card updated (for open PRs to `trunk`, **In Review** is set by the workflow if the issue is on the project and the PAT secret is configured).
* Local checks passed: see [Pre-push checks](pre-push-checks.md) and `AGENTS.md` for client/server.
