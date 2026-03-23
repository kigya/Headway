---
icon: code-branch
---

# Git Conventions

## Headway Contribution Conventions

This repository contains both client and server code. To keep history searchable and automation-friendly, we use consistent conventions for branches, commits, pull requests, and issue tracking.

### Branching model: Trunk-Based Development

We use Trunk-Based Development (TBD) with a single long-lived mainline branch:

* Mainline branch: `trunk`
* Work happens in short-lived branches created from `trunk`
* Changes are merged back into `trunk` via Pull Requests

Guidelines:

* Keep branches small and focused (prefer hours/days, not weeks).
* Merge frequently; avoid “big bang” PRs.

### Commit message convention

Commit messages must include the issue number from the project board.

Client commits:

* `CLIENT-HEADWAY-<NUMBER>: <SHORT SUMMARY>`

Server commits:

* `SERVER-HEADWAY-<NUMBER>: <SHORT SUMMARY>`

Fullstack commits (changes include both client and server as part of the same task):

* `FULLSTACK-HEADWAY-<NUMBER>: <SHORT SUMMARY>`

Rules:

* `<NUMBER>` is the task number from the board.
* `<SHORT SUMMARY>` is a short description of the change (use plain English; keep it concise).
* Prefer one logical change per commit.
* Use `FULLSTACK-HEADWAY-*` only when the change set genuinely spans both client and server for the same task. If a commit is strictly client-only or server-only, use the corresponding prefix.

Examples:

* `CLIENT-HEADWAY-16: Updated README with docs`
* `SERVER-HEADWAY-8: Added healthcheck endpoint`
* `FULLSTACK-HEADWAY-32: Add stage/prod env switch for server and client`
* `CLIENT-HEADWAY-21: Fixed navigation crash on cold start`

### Branch naming convention

Branches must include the issue number and a short description.

Client branches:

* `client-headway/<NUMBER>-<short-summary>`

Server branches:

* `server-headway/<NUMBER>-<short-summary>`

Fullstack branches (work includes both client and server as part of the same task):

* `fullstack-headway/<NUMBER>-<short-summary>`

Rules:

* Use kebab-case for `<short-summary>`.
* Keep it short; avoid special characters.
* Use the `fullstack-headway/` prefix only when the branch contains intended changes in both client and server for the same task.

Examples:

* `client-headway/16-readme-docs`
* `server-headway/8-healthcheck`
* `fullstack-headway/32-stage-prod-env-switch`
* `client-headway/21-fix-navigation-crash`

### Pull Request (PR) title convention

PR titles must follow the same pattern as commit messages:

Client PR title:

* `CLIENT-HEADWAY-<NUMBER>: <SHORT SUMMARY>`

Server PR title:

* `SERVER-HEADWAY-<NUMBER>: <SHORT SUMMARY>`

Fullstack PR title (changes include both client and server):

* `FULLSTACK-HEADWAY-<NUMBER>: <SHORT SUMMARY>`

Note:

* GitHub shows the PR number automatically in the UI (e.g., `#17`). You should not rely on the PR number as an identifier; the task number is the stable reference.

Examples:

* `CLIENT-HEADWAY-16: Updated README with docs`
* `SERVER-HEADWAY-8: Added healthcheck endpoint`
* `FULLSTACK-HEADWAY-32: Add stage/prod env switch across client and server`

### Project board workflow

Project board view:

* https://github.com/users/kigya/projects/4/views/2

Columns and meaning:

* **To Do** — task is created but not started
* **In Progress** — work is actively ongoing
* **In Review** — PR is opened and awaiting review/changes
* **Blocked** — work cannot proceed (dependency/decision/access missing)
* **Done** — completed and merged (or otherwise finished)

Recommended flow:

* Create/confirm task → **To Do**
* Start work + create branch → **In Progress**
* Open PR → **In Review**
* If blocked → **Blocked** (add context in task/PR)
* Merge → **Done**

### Labels (tags) on tasks/PRs

Mandatory domain label:

* For server work: must add `Headway-Backend`
* For client work: must add `Headway-Client`
* For fullstack work (client + server in the same task/PR): add **both** `Headway-Client` and `Headway-Backend`

Common additional labels (use when applicable):

* `feature` — product feature
* `bug` — defect fix
* `tech` — technical setup / refactoring / internal improvements
* `research` — investigation / spike / exploration
* `draft` — PR is not ready for review yet

Suggested labeling pattern:

* Domain: `Headway-Client` and/or `Headway-Backend` (mandatory)
* Type: `feature` / `bug` / `tech` / `research`
* State (optional): `draft` while PR is incomplete

Examples:

* Client feature: `Headway-Client` + `feature`
* Server bugfix: `Headway-Backend` + `bug`
* Fullstack feature: `Headway-Client` + `Headway-Backend` + `feature`
* Refactor: `Headway-Client` + `tech`
* Spike: `Headway-Backend` + `research`

### Quick checklist

Before opening a PR:

* Branch name matches convention (`client-headway/...`, `server-headway/...`, or `fullstack-headway/...`)
* Commits include correct prefix and board task number
* PR title matches convention
* Task/PR has correct mandatory domain label(s):
  * client-only: `Headway-Client`
  * server-only: `Headway-Backend`
  * fullstack: both `Headway-Client` and `Headway-Backend`
* Task column is updated (typically **In Review** once PR is opened)
