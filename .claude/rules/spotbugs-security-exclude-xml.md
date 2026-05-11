# SpotBugs Exclude File Ordering

**Applies to:** `spotbugs-security-exclude.xml` (and any other `<FindBugsFilter>` file in this repo).

## Block order

Sort `<Match>` blocks by:

1. `<Bug pattern="...">` (ASCII)
2. `<Class name="...">` (ASCII; package dots compare segment-by-segment)
3. `<Method name="...">` (ASCII)

## Child order

Inside each `<Match>`, order children alphabetically: `Bug`, `Class`, `Method`.

## Comments

Any `<!-- ... -->` block above a `<Match>`, separated only by blank lines, is part of that entry and moves with it.

**Separation:** Leave exactly one blank line between the comment and its `<Match>`. This matches the existing file's style.

**Single-line comments** stay on one line: `<!-- text -->`.

**Multi-line comments** put the opener (`<!--`) and closer (`-->`) on their own lines, at the same indent as the `<Match>`. Body lines use the same indent as the opener and closer (no extra alignment to the `<!--` text).

**Width:** Each line (including indent, where a tab counts as 1 character) is at most 120 characters. Use the single-line form when `<!-- text -->` plus indent fits within 120; otherwise switch to multi-line. Multi-line body lines also wrap at 120 characters, broken on word boundaries with greedy fill.

```xml
<!--
False positive: getVersion performs OSGi format detection, not security validation.
The replaceAll is internal version string formatting (dots to underscores), not
untrusted input handling, so there is no security bypass risk.
-->

<Match>
    ...
</Match>
```

## Preserve

- Indentation (tabs)
- Attribute values (don't rewrite, only reorder)
- The surrounding `<FindBugsFilter>` wrapper
- The XML declaration if present
- No trailing newline at end of file (closing `</FindBugsFilter>` is the last byte)

## Edge case: non-standard children

If a `<Match>` uses elements other than `Bug`/`Class`/`Method` (e.g. `<Or>`, `<Field>`, `<Source>`), keep those at the end of the child list in their original relative order, and use the first `Bug`/`Class`/`Method` value (or empty string if absent) as the sort key. Flag the unusual entry to the user.

## Example

**Before:**
```xml
<Match>
    <Class name="com.example.Foo" />
    <Method name="bar" />
    <Bug pattern="HARD_CODE_PASSWORD" />
</Match>

<!-- comment -->

<Match>
    <Class name="com.example.Baz" />
    <Method name="qux" />
    <Bug pattern="HARD_CODE_PASSWORD" />
</Match>
```

**After:**
```xml
<!-- comment -->

<Match>
    <Bug pattern="HARD_CODE_PASSWORD" />
    <Class name="com.example.Baz" />
    <Method name="qux" />
</Match>

<Match>
    <Bug pattern="HARD_CODE_PASSWORD" />
    <Class name="com.example.Foo" />
    <Method name="bar" />
</Match>
```
