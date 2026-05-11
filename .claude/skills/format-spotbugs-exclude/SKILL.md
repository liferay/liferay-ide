---
name: format-spotbugs-exclude
description: Format `spotbugs-security-exclude.xml` (or another `<FindBugsFilter>` file in this repo) according to the rule at `.claude/rules/spotbugs-security-exclude-xml.md`. Use when the user asks to format or normalize the SpotBugs exclude file, or when adding entries to it.
---

# Format SpotBugs Exclude Filter

Apply the rule defined in `.claude/rules/spotbugs-security-exclude-xml.md` to a `<FindBugsFilter>` XML file.

## Usage

- `/format-spotbugs-exclude` — format `spotbugs-security-exclude.xml` at the repo root
- `/format-spotbugs-exclude <path>` — format the given filter file

## Process

1. **Read the rule.** Read `.claude/rules/spotbugs-security-exclude-xml.md` for the canonical block order, child order, uniqueness requirement, comment handling, and preservation requirements. The rule file is the source of truth — do not paraphrase it from memory.
2. **Read the target file.**
3. **Check for `<Or>` blocks.** Each `<Match>` must target a single finding location. If any `<Match>` contains an `<Or>` element, stop and ask the user to split it into separate entries (each new entry needs its own comment) before continuing — do not auto-split, since the per-entry comment requires human judgment.
4. **Parse** into a list of (leading-comment, `<Match>`) pairs plus any header/footer (XML declaration, `<FindBugsFilter>` open/close tags).
5. **Reorder children** inside each `<Match>` per the rule.
6. **Sort the pairs** per the rule.
7. **Re-emit** the file with one blank line between entries, comments above their `<Match>`, and the original wrapper preserved.
8. **Verify** with `git diff <path>` — the diff should be purely a reordering. No attribute or comment text changes. If anything else changed, stop and investigate.