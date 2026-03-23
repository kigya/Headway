---
icon: forward
---

# Live Templates

We ship shared Live Templates so you don’t have to import them manually.

### One-time install

```
./gradlew installLiveTemplates
```

This copies config/templates/\*.xml into your Android Studio templates folder.

Restart Android Studio (or _File → Invalidate Caches / Restart_) to see them under Settings → Editor → Live Templates (group: Headway).

### If detection fails

Specify your IDE config dir explicitly:

```
./gradlew installLiveTemplates -PasConfigDir="$HOME/Library/Application Support/Google/AndroidStudio2025.1"
```

### Manual alternative

Copy the XMLs yourself and restart IDE:

* macOS: \~/Library/Application Support/Google/AndroidStudio\<ver>/templates/
* Linux: \~/.config/Google/AndroidStudio\<ver>/templates/
* Windows: C:\Users\\\<you>\AppData\Roaming\Google\AndroidStudio\<ver>\templates\\
