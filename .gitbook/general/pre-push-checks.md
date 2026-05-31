---
icon: seedling
---

# Pre-push checks

The goal is to avoid pushing obviously broken code and catch detekt/lint issues earlier.

## Installing git hooks (once)

Script `config/git/hooks/installer.gradle.kts` is applied in both **`client/build.gradle.kts`** and **`server/build.gradle.kts`**. Install hooks from **either** directory:

```
cd client && ./gradlew installGitHooks
```

or

```
cd server && ./gradlew installGitHooks
```

This sets `core.hooksPath` to `config/git/hooks` and makes hook files executable (on Unix).

## What pre-push does

Script `config/git/hooks/pre-push` runs `./gradlew detekt` and `./gradlew lint` from the **current git working directory**. There is no root-level `gradlew` in the monorepo: if you push from the repo root and the hook fails with “gradlew not found”, run checks manually (below) or adjust the hook for your workflow.

## Manual checks (reliable)

Before push, run:

```
cd client && ./gradlew detekt
cd server && ./gradlew detekt
```

If you only changed one side, the matching command is enough. For substantial client changes, also:

```
cd client && ./gradlew app:headwayAndroid:assembleDebug
```

For server:

```
cd server && ./gradlew build
```

## Bypassing the hook (rare)

```
git push --no-verify
```

Use only deliberately (e.g. an agreed emergency hotfix).

## Alternative: hooks path only

```
git config core.hooksPath config/git/hooks
chmod -R +x config/git/hooks
```

Verify:

```
git config --get core.hooksPath
```
