---
icon: arrows-rotate
---

# githubEnvSync

Gradle plugin in **`server/build-logic/github-env-sync`**. Applied in **`server/build.gradle.kts`**. It **pulls variables from GitHub** (repository variables + **dev** / **prod** environment variables) and **generates files** in `server/docker/` from templates.

## Purpose

GitHub holds values per environment. Locally we do not hand-maintain final `env.*` secrets: the repo keeps **`*.template`** files in `server/docker/template/`, and the plugin substitutes placeholders and writes results (e.g. `env.common`, `env.gateway`, …) to the output directory.

## Project configuration

In `server/build.gradle.kts`, roughly:

* repository **`kigya/Headway`**;
* templates: `server/docker/template/`;
* output: `server/docker/` (project docker root);
* environments: **`dev`** and **`prod`**;
* Gradle properties for token and username: `github.env.sync.token`, `github.env.sync.username`;
* build may fail if a variable is missing in GitHub (`failOnMissingVariables`).

## Gradle tasks

| Task | Purpose |
|------|---------|
| **`githubLogin`** | GitHub OAuth login, saves token in user Gradle properties (may open a browser). |
| **`syncGithubEnv`** | Reads variables from GitHub API and renders templates. |
| **`githubEnvSync`** | Alias that depends on `syncGithubEnv`. |

Task group in Gradle: **github env sync**.

## Internal flow

1. Token is read from Gradle user properties (after `githubLogin`).
2. Repository permission is checked (collaborator access at least read).
3. **Repository variables** load, then **environment variables** per target environment; maps merge (environment overrides repo on name clash).
4. For each `*.template` in `server/docker/template/`, text is read, values substituted, output written with the same name **without** `.template` in the output directory.
5. Sync state is cached (file **`.github-env-sync.state`**) — reruns are skipped if templates and remote variables are unchanged.

## IDE sync

With **`runOnIdeSync`** enabled (default yes), Gradle IDE sync may **automatically** run login and sync when generated files are stale. On failure a warning is logged and project sync may still succeed; run manually: `githubLogin`, then `syncGithubEnv` / `githubEnvSync`.

## Developer steps

1. Once: **`cd server && ./gradlew githubLogin`** (grant repo access if prompted).
2. When GitHub variables or templates change: **`cd server && ./gradlew githubEnvSync`**.
3. Do not commit secrets; templates and docs may be committed; substituted secret values stay local/CI per your policy.

If Gradle reports environments are not configured, check `githubEnvSync { environments.set(listOf("dev", "prod")) }` (or legacy `environment`).
