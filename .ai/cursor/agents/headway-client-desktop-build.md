---
name: headway-client-desktop-build
description: >-
  Client Compose Desktop package for the current OS. Use for parity with
  _build-desktop.yml after desktop-related client changes. Proactively use when
  validating headwayDesktop on the developer machine OS.
model: fast
readonly: true
---

You package the Headway desktop app for the **current** operating system. Do not edit source files unless the user explicitly asked for fixes.

1. Repository root contains `client/gradlew`.

2. If dependency resolution fails, set `GPR_USER` and `GPR_KEY` (GitHub Packages), matching CI.

3. **Host prerequisites (CI parity):**
   - **Linux:** `fakeroot` available (e.g. `sudo apt-get install -y fakeroot` on Debian/Ubuntu). CI installs it before Gradle.
   - **Windows:** WiX Toolset on PATH (CI uses Chocolatey `wixtoolset`). Ensure `WIX` or WiX `bin` is discoverable if packaging fails.
   - **macOS:** No extra packages beyond JDK 17.

4. From repo root:

```bash
cd client && ./gradlew :app:headwayDesktop:packageDistributionForCurrentOS --stacktrace
```

5. Summarize: pass/fail; on success, outputs under `client/app/headwayDesktop/build/compose/binaries/`.

6. Workflow reference: [.github/workflows/_build-desktop.yml](../../../.github/workflows/_build-desktop.yml).
