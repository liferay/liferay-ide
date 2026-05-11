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

1. **Read the rule.** Read `.claude/rules/spotbugs-security-exclude-xml.md` for the canonical block order, child order, comment handling, and preservation requirements. The rule file is the source of truth — do not paraphrase it from memory.
2. **Read the target file.**
3. **Parse** into a list of (leading-comment, `<Match>`) pairs plus any header/footer (XML declaration, `<FindBugsFilter>` open/close tags).
4. **Reorder children** inside each `<Match>` per the rule.
5. **Sort the pairs** per the rule.
6. **Re-emit** the file with one blank line between entries, comments above their `<Match>`, and the original wrapper preserved.
7. **Verify** with `git diff <path>` — the diff should be only reordering and comment formatting. No attribute changes. If anything else changed, stop and investigate.
