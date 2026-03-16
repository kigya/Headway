# agents.md — Headway Project Guidelines

This document provides general guidelines for assistants and contributors working on the Headway project.

## 1. Code Quality and Style

### 1.1 No code comments
This codebase follows a "no comments" policy. Do not use single-line (`//`) or multi-line (`/* */`) comments to explain what the code is doing. The code should be self-describing through:
- expressive naming (functions, variables, internal enums),
- clear layout structure,
- KDoc for public APIs (allowed and encouraged for documentation).

Avoid redundant comments like: `// Derive the stub kind; null means "show normal content"`.

### 1.2 Naming Conventions
- Use standard Kotlin naming conventions (camelCase for variables/functions, PascalCase for classes).
- Booleans should be prefixed with `is` or `should` (e.g., `isEnabled`, `shouldRetry`).

### 1.3 Design System First
- All UI development must use the `core/design-system` module.
- Never use raw colors or dimensions in feature modules.
- Refer to `core/design-system/src/commonMain/kotlin/dev/kigya/headway/core/designSystem/AGENTS.md` for specific design system rules.
