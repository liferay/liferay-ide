# SpotBugs Exclude File Format

**Applies to:** `spotbugs-security-exclude.xml` (and any other `<FindBugsFilter>` file in this repo).

## Uniqueness

Each `<Match>` block must target a single, unique finding location.

**Do not use `<Or>` to combine multiple targets into one block.** When multiple methods (or other variants) on the same class need the same exclusion, write one `<Match>` per target, each with its own comment.

Why: splitting makes every exclusion individually auditable and traceable to a specific call site. An `<Or>` block hides per-finding justifications behind a single shared comment and is harder to diff or review one finding at a time.

**Wrong:**
```xml
<Match>
    <Bug pattern="UNENCRYPTED_SOCKET" />
    <Class name="com.example.SocketUtil" />
    <Or>
        <Method name="canConnect" />
        <Method name="canConnectProxy" />
    </Or>
</Match>
```

**Right:**
```xml

<!-- Comment specific to canConnect -->

<Match>
    <Bug pattern="UNENCRYPTED_SOCKET" />
    <Class name="com.example.SocketUtil" />
    <Method name="canConnect" />
</Match>

<!-- Comment specific to canConnectProxy -->

<Match>
    <Bug pattern="UNENCRYPTED_SOCKET" />
    <Class name="com.example.SocketUtil" />
    <Method name="canConnectProxy" />
</Match>
```

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

**Wording:** State the reason directly — no `Skip this X because...` or `This is a false positive because...` preamble. Lead with the category or the fact, then a short justification.

**Wrong** (verbose preamble): `<!-- Skip this unencrypted socket because the Gogo OSGi console is a telnet protocol (per class Javadoc) and the server endpoint is not SSL -->`

**Right** (direct): `<!-- Gogo OSGi console uses the telnet protocol (per class Javadoc); endpoint is not SSL -->`

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

`<Or>` is **not allowed** — see the Uniqueness section; split the entry instead.

If a `<Match>` uses other non-standard elements (e.g. `<Field>`, `<Source>`), keep those at the end of the child list in their original relative order, and use the first `Bug`/`Class`/`Method` value (or empty string if absent) as the sort key. Flag the unusual entry to the user.

## Example

**Before:**
```xml

<!-- comment -->

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

<!-- comment -->

<Match>
    <Bug pattern="HARD_CODE_PASSWORD" />
    <Class name="com.example.Foo" />
    <Method name="bar" />
</Match>
```
