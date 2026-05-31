---
icon: forward
---

# Live Templates

Shared Live Templates live in the repository. Task `installLiveTemplates` is defined in `config/templates/installer.gradle.kts` and applied in both **`client/build.gradle.kts`** and **`server/build.gradle.kts`**, so you can run it **from `client/` or `server/`** with the same effect.

## Installation

```
cd client && ./gradlew installLiveTemplates
```

or

```
cd server && ./gradlew installLiveTemplates
```

Files are copied into the Android Studio templates directory. Restart the IDE (or **File → Invalidate Caches / Restart**). Find templates under **Settings → Editor → Live Templates**, group **Headway**.

## If IDE directory auto-detection fails

Set the config directory manually (macOS example):

```
cd client && ./gradlew installLiveTemplates -PasConfigDir="$HOME/Library/Application Support/Google/AndroidStudio2025.1"
```

(same with `cd server && …`.)

## Manual installation

Copy XML from `config/templates/` into your Android Studio version templates folder:

* macOS: `~/Library/Application Support/Google/AndroidStudio<version>/templates/`
* Linux: `~/.config/Google/AndroidStudio<version>/templates/`
* Windows: `%APPDATA%\Google\AndroidStudio<version>\templates\`
