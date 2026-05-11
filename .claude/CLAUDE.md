# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Liferay IDE is a collection of Eclipse plugins for developing Liferay Portal plugins (portlets, hooks, themes, service builders, etc.). It is built as an Eclipse/Tycho project using Maven, producing an Eclipse p2 update site.

## Build Commands

### Build (skip tests)
```
./mvnw clean package -DskipTests
```
Or equivalently: `./build-updatesite.sh`

### Build with tests
```
./mvnw clean verify -Dliferay.bundles.dir="./tests-resources"
```
Or: `./run-tests.sh` (CI version that uses `$GITHUB_WORKSPACE/liferay-ide-m2-repository`)

### Check source formatting (read-only)
```
cd build/com.liferay.ide.build.source.formatter && ./csf.sh
```

### Auto-fix source formatting
```
cd build/com.liferay.ide.build.source.formatter && ./sf.sh
```

### Run security scan (SpotBugs + FindSecBugs)
```
./run-security-scan.sh
./run-security-scan.sh --module tools/plugins/com.liferay.ide.core # single module
./run-security-scan.sh --bug-type XXE_SAXPARSER # filter by bug type
```

### SpotBugs exclude file format
`spotbugs-security-exclude.xml` is kept formatted per `.claude/rules/spotbugs-security-exclude-xml.md`. The `/format-spotbugs-exclude` skill applies that rule.

## Architecture

### Build System
- **Maven + Tycho 3.0.5**: All modules use `eclipse-plugin`, `eclipse-feature`, `eclipse-test-plugin`, or `eclipse-repository` packaging types managed by Tycho
- **Maven Wrapper**: Use `./mvnw` (Maven 3.6.3). Do not use a system-installed Maven
- **Parent POM**: `build/parent/pom.xml` defines Tycho version, Eclipse/p2 repository URLs, and shared build config
- **Root POM** (`pom.xml`): Aggregates `build/parent` and `build` modules; also configures SpotBugs plugin management
- **Build POM** (`build/pom.xml`): Defines profiles (`default`, `skipTests`, `installers`) that aggregate the main module groups

### Module Structure
Each module follows Eclipse OSGi plugin conventions (`META-INF/MANIFEST.MF`, `plugin.xml`, `build.properties`).

- **`tools/plugins/`** - Core Eclipse plugins (`com.liferay.ide.core`, `com.liferay.ide.project.core`, `com.liferay.ide.gradle.core`, `com.liferay.ide.server.core`, etc.)
- **`tools/features/`** - Eclipse feature definitions that group plugins for installation
- **`tools/tests/`** - Unit tests (e.g., `com.liferay.ide.core.tests`, `com.liferay.ide.project.core.tests`)
- **`maven/plugins/`** - Maven integration plugins (`com.liferay.ide.maven.core`, `com.liferay.ide.maven.ui`)
- **`maven/tests/`** - Maven integration tests
- **`enterprise/plugins/`** - Enterprise/DXP-specific plugins (Kaleo workflow, scripting, portal, studio UI)
- **`portal/`** - Portal-specific plugins and tests
- **`functional-tests/`** - SWTBot-based UI functional tests organized by feature area (fragment, hook, module, server, etc.)
- **`integration-tests/`** - Integration tests
- **`build/com.liferay.ide-repository/`** - Assembles the p2 update site from all features
- **`tests-resources/`** - Shared test fixture projects (Liferay workspaces, sample portlets, etc.)

### Plugin Naming Convention
- `com.liferay.ide.<area>.core` - Headless logic (models, operations, builders)
- `com.liferay.ide.<area>.ui` - Eclipse UI contributions (wizards, editors, views)
- Areas include: `project`, `gradle`, `maven`, `server`, `hook`, `portlet`, `service`, `layouttpl`, `theme`, `sdk`, `kaleo`

### Source Formatting
The project uses Liferay Source Formatter (invoked via Maven plugin) with rules defined in `source-formatter.properties` and suppressions in `source-formatter-suppressions.xml`. CI checks formatting on every PR.

### CI (GitHub Actions)
- **verify.yml** (on PR): Runs source formatting check, test verification, and SpotBugs security scan
- **publish.yml** (on push to master): Builds update site and deploys to S3

### Java Version
- Build and tests require **JDK 21** (CI uses Zulu 21)
- Source formatting check uses JDK 17