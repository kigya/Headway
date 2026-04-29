---
name: capture-lesson
description: >-
  Captures a reusable Headway mistake into .ai/cursor/rules/lessons-learned.mdc
  after a fix (or skips when one-off) and updates .ai/cursor/memory-bank/ when
  the lesson changes ongoing context. Use when the user runs /capture-lesson
  or asks to record a lesson from a correction.
disable-model-invocation: true
---

# Capture lesson

## Instructions

1. Use the current chat context and the user’s description. When available, inspect recent changes (e.g. diff or touched files) in **read-only** fashion.
2. State the **root cause** in one short phrase.
3. Classify **reusable** vs **one-off**:
   - **Reusable:** Violates architecture or patterns in [AGENTS.md](../../../AGENTS.md) or [.ai/cursor/rules/](../../rules/); stable project pattern; likely to recur; spans more than a trivial typo; expressible as a general rule.
   - **One-off:** Typo; single-screen copy; one-off business rule; rename with no broader lesson; isolated case with no rule value.
4. If **one-off:** Reply **skipped** with one line of reasoning. Do **not** edit [lessons-learned.mdc](../../rules/lessons-learned.mdc).
5. If **reusable:**
   - Read [.ai/cursor/rules/lessons-learned.mdc](../../rules/lessons-learned.mdc).
   - If the **same root cause** already exists, **merge**: tighten Rule / Why / Bad / Correct for that `### N` instead of adding a near-duplicate.
   - Otherwise append a new lesson as the next `### N` using the file’s template:
     - **Rule:**
     - **Why it exists:**
     - **Bad pattern (short):**
     - **Correct pattern (short):**
   - Keep `---` separators between lessons consistent with the file.
   - After merge or insert, ensure headings are numbered **`### 1` through `### N`** in order with no gaps.
6. If **reusable**, evaluate **Memory Bank** ([`.ai/cursor/memory-bank/`](../../memory-bank/)):
   - If the lesson changes **ongoing project context** (e.g. stack fact, cross-cutting pattern, current focus), update the smallest set of files—typically [`activeContext.md`](../../memory-bank/activeContext.md), [`systemPatterns.md`](../../memory-bank/systemPatterns.md), or [`techContext.md`](../../memory-bank/techContext.md)—with **short** deltas. Do not paste the full lesson; link or restate one line.
   - If Memory Bank already reflects it, skip.
7. Reply concisely: **added** (new lesson), **merged** (cite `### N`), or **skipped** (already covered or one-off); note **Memory Bank** if touched.

## Output

- One short paragraph: outcome + which lesson number if merged + any Memory Bank file updated.

## Constraints

- Do **not** edit [AGENTS.md](../../../AGENTS.md), [client/AGENTS.md](../../../client/AGENTS.md), or [server/AGENTS.md](../../../server/AGENTS.md) in this workflow.
- Do **not** add scripts, hooks, or CI.
