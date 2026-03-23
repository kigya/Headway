---
icon: seedling
---

# Pre-push checks

To avoid wasting CI resources, we run quick static checks before pushing.

### One-time setup

```
./gradlew installGitHooks
```

This sets core.hooksPath to config/git/hooks and makes the hook executable.

### What happens on push

The hook runs:

```
./gradlew detekt --console=plain
./gradlew lint
```

If either fails, the push is blocked with the error output.

### Manual install (alternative)

```
git config core.hooksPath config/git/hooks
# macOS/Linux only:
chmod -R +x config/git/hooks
```

### Verify / Bypass

```
git config --get core.hooksPath   # should print: config/git/hooks
git push --no-verify              # bypass (use sparingly)
```
