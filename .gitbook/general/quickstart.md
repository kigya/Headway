---
icon: bolt
---

# Quick start

## Repository

[github.com/kigya/Headway](https://github.com/kigya/Headway)

## Cloning

1. In Android Studio: **Get from VCS** (or **File → New → Project from Version Control…**).
2. URL: `https://github.com/kigya/Headway.git`.
3. Choose a folder and click **Clone** (sign in to GitHub or use a token if prompted).

## Git: name and email

In the IDE terminal:

Globally (all repositories):

```
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

For this repository only (from the clone root):

```
git config user.name "Your Name"
git config user.email "you@example.com"
```

Verify:

```
git config --list --show-origin
```

## Monorepo layout

* `client/` — KMP app (Android, iOS, Desktop, Web), Compose, MVIKotlin.
* `server/` — Ktor services (auth, database, home, gateway, admin, etc.), Gradle, Docker.
* `AGENTS.md` (root) — shared Kotlin rules for the whole repo.
* `client/AGENTS.md` and `server/AGENTS.md` — side-specific details.
* `.ai/cursor/rules/` and `.ai/cursor/skills/` — IDE and agent hints (they do not replace `AGENTS.md`).

## Minimum commands after changes

**Client** (main check before handing off a task):

```
cd client && ./gradlew app:headwayAndroid:assembleDebug
cd client && ./gradlew detekt
```

**Server:**

```
cd server && ./gradlew build
cd server && ./gradlew detekt
```

Next: set up hooks, the task board, and branch conventions — see [Git, PRs, labels, and task board](git-and-board.md).
