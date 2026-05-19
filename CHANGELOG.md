# Changelog

All notable changes to this project will be documented in this file.

The format is a modified version of [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

- `Added` - for new features.
- `Changed` - for changes in existing functionality.
- `Improved` - for enhancement or optimization in existing functionality.
- `Removed` - for now removed features.
- `Fixed` - for any bug fixes.
- `Other` - for technical architectural updates.

## [Unreleased]

### Added
-

## [1.0.0] - 2026-05-19

### Added
- **Visual Rebrand Overhaul:** Transformed the entire user interface identity from the legacy system to **MiruKan**. This includes designing and embedding native custom app icons, updated splash layout architectures, and relative asset matrices.
- **Neon Theme Engine:** Refactored the core global color mapping sheets to replace stock styles with a striking dark-navy base paired with high-contrast lime green focus accents across layout layers.
- **Download Queue Size Indicators:** Integrated real-time data metrics within the download manager queue view, displaying an explicit size counter tracking current downloaded bytes alongside total file sizes for ongoing streams.

### Changed
- **Application ID Shift:** Migrated the core Android system configuration packaging layer, explicitly changing the unique `applicationId` identifier from the legacy upstream namespace (`eu.kanade.tachiyomi`) to your custom standalone domain structure.
- **Project Context Initialization:** Updated the initialization configuration in `settings.gradle.kts` to identify the root project structure natively as **MiruKan** rather than inheriting the legacy naming footprint.
- **CI Artifact Retrieval Target:** Reconfigured the GitHub Actions artifact upload path patterns to capture standard production-signed production binaries (`*-release.apk`) rather than legacy unsigned outputs.
- **Unified Split APK Extraction:** Swapped strict individual architecture targets for a dynamic wildcard matcher (`*.apk`) to cleanly wrap and bundle all 5 split compilation variants (Universal, ARM64, ARMEABI, X86, X86_64) into the CI distribution archive.

### Fixed
- **CI Pipeline Constraints:** Stripped hardcoded conditional guards within the GitHub Actions configuration files that locked deployment executions exclusively to the upstream repository, routing builds to target your custom environment workspace seamlessly.
- **Update Engine Targeting:** Recoupled the internal OTA update system, remapping the hardcoded network API endpoints to scan your personal repository data pipelines for fresh asset artifacts instead of dead upstream references.
- **CI Sequence and Variable Contexts:** Reordered the execution matrix in `build_push.yml` to compute environment tags before bundling steps, fixed broken context scoping declarations (`env.VERSION_TAG`), and stabilized the artifact packaging tasks to prevent race conditions during file migrations.
- **Pull Request Automation Structural Faults:** Resolved YAML validation issues, broken duplicate configuration keys, and empty hanging steps inside the `build_pull_request.yml` configuration tree to ensure reliable contributors verification testing.

### Improved
- **Download Core:** Enhanced overall download queue stability and background thread reordering behavior ([@user4302](https://github.com/user4302)).

### Removed
- **Redundant Third-Party Signing:** Eliminated the legacy external `r0adkll/sign-android-release` pipeline action to clear out multi-step build processing overhead.

### Other
- **Semantic Version Alignment:** Transitioned the project tracking matrix away from the downstream 4-digit layout to native 3-digit Semantic Versioning (`v1.0.0`) to establish a clean standalone maintenance baseline.
- **Project Foundation:** Officially established independent fork branch structures to ensure long-term framework preservation and independent issue remediation.
- **Native Gradle Key Signing Infrastructure:** Integrated an end-to-end automated app signing architecture directly within `build.gradle.kts`, using runtime environment injection (`SIGNING_KEY_FILE`, `KEY_STORE_PASSWORD`, `ALIAS`) to compile fully production-signed APK outputs natively during the core build phase.
