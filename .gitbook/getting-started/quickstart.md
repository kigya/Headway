---
icon: bolt
---

# Quickstart

Repo link: [https://github.com/kigya/Headway](https://github.com/kigya/Headway)

### Clone in Android Studio

1. Open Android Studio → Get from VCS (or _File → New → Project from Version Control…_).
2. Paste: https://github.com/kigya/Headway.git.
3. Choose a folder and click Clone (sign in to GitHub or use a token if prompted).

### Configure your Git identity

Open Terminal in Android Studio (View → Tool Windows → Terminal) and run:

* Globally (all repos):

```
git config --global user.name "Your Name"
git config --global user.email "you@example.com"
```

* Only for this repo (run inside the project folder):

```
git config user.name "Your Name"
git config user.email "you@example.com"
```



Verify:

```
git config --list --show-origin
```

You’re set—proceed to platform-specific run instructions (Android/iOS/Desktop/Web).
