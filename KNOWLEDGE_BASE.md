# Knowledge Base

> Repository of stack-specific edge cases, resolved complex bugs, anti-patterns, and reusable project solutions.
> **Do not populate with generic framework definitions.**

## How to use

1. Add entries only after resolving a non-obvious issue specific to this project.
2. Include: symptom, root cause, fix, and prevention.
3. Link to relevant ADRs or PRs when available.

## Entries

### KB-001 — UTF-16 file corruption on Windows

| Field | Detail |
|-------|--------|
| **Symptom** | `check-json` / `npm` / `json.load` fails; git ignore rules stop working; `.gitignore` shows as untracked patterns not applied |
| **Cause** | Cursor `StrReplace` or Windows editor saves text as UTF-16 LE (NUL bytes between ASCII chars) |
| **Fix** | Rewrite affected files with Python `Path.write_text(..., encoding='utf-8')`; re-run `scripts/check-file-encoding.sh` |
| **Prevention** | Bulk edits on Windows via Python/PowerShell UTF-8 write; include root `.gitignore` in encoding scan |

### KB-002 — Invalid `trivy-action@0.28.0` ref

| Field | Detail |
|-------|--------|
| **Symptom** | Security Scan workflow fails at setup: action version not found |
| **Cause** | Bare semver `@0.28.0` is not a valid GitHub Action ref tag |
| **Fix** | Pin to full SHA: `aquasecurity/trivy-action@a9c7b0f06e461e9d4b4d1711f154ee024b8d7ab8 # v0.36.0` |
| **Prevention** | Run `validate-workflow-actions.sh` pre-push; use `check-workflow-action-ref-format.sh` locally |

### KB-003 — `gh api --silent` false CI failures

| Field | Detail |
|-------|--------|
| **Symptom** | `validate-workflow-actions.sh` fails in CI with unknown `gh` flag error |
| **Cause** | `gh api` has no `--silent` flag; stderr not suppressed correctly |
| **Fix** | Redirect to `/dev/null` instead: `gh api ... >/dev/null 2>&1` |
| **Prevention** | Test validation scripts in CI job with `GH_TOKEN`; avoid undocumented `gh` flags |

### KB-004 — Lighthouse performance flake on shared runners

| Field | Detail |
|-------|--------|
| **Symptom** | CI fails with performance 0.88 vs required 0.90 on a single Lighthouse run |
| **Cause** | GitHub-hosted runner CPU variance; single-run assertion is noisy |
| **Fix** | Set `numberOfRuns: 3` in `.lighthouserc.json`; LHCI uses median; keep `minScore: 0.9` |
| **Prevention** | Do not lower performance budget for CI flake; use multi-run median in `modules/web/MODULE.md` |

### KB-005 — Playwright webServer duplicate build

| Field | Detail |
|-------|--------|
| **Symptom** | E2E hangs or serves stale assets; double `vite build` in CI |
| **Cause** | `webServer` runs build while CI already built; wrong host binding |
| **Fix** | Use `vite preview` on `127.0.0.1`; CI runs `npm run build` once before Playwright |
| **Prevention** | Golden Path `examples/web/playwright.config.ts` documents preview-only webServer |

### KB-006 — TypeScript strict null in render handlers

| Field | Detail |
|-------|--------|
| **Symptom** | `tsc` / ESLint error: Object is possibly null inside `render()` callback |
| **Cause** | `strictNullChecks` + `document.getElementById` return type includes null |
| **Fix** | Assign narrowed ref at module scope: `const root = document.getElementById('root')!` or guard once |
| **Prevention** | Module-level `const root = app` pattern in `examples/web/src/main.ts` |

### KB-007 — npm/pip overrides policy for transitive CVEs

| Field | Detail |
|-------|--------|
| **Symptom** | Dependabot or `npm audit` / `uv pip audit` reports CVE in a transitive dependency with no direct upgrade path |
| **Cause** | Parent package pins or bundles a vulnerable sub-dependency; fix not yet published upstream |
| **Fix** | **npm:** add `overrides` in `package.json` to force patched semver (see `examples/web` `@lhci/cli` overrides). **Python:** prefer `uv`/`pip` constraint or bump direct dep; document in DECISION_LOG if override is temporary |
| **Prevention** | Prefer overrides over `--force` installs; remove overrides when upstream ships fix; weekly triage per `docs/SECURITY_TRIAGE.md`; see KB-007 before dismissing Dependabot alerts |

### KB-009 — Release Please `pr` output is JSON, not a PR number

| Field | Detail |
|-------|--------|
| **Symptom** | `release-please.yml` sync step fails: `syntax error near unexpected token '('` on `gh pr checkout` |
| **Cause** | `steps.release.outputs.pr` is a JSON PullRequest object string, not the numeric PR id |
| **Fix** | Guard with `prs_created == 'true'`; use `fromJSON(steps.release.outputs.pr).number` for `gh pr checkout` |
| **Prevention** | See release-please-action outputs table; never pass `outputs.pr` directly to shell commands |

### KB-008 — `android-release` APK hash compare policy

| Field | Detail |
|-------|--------|
| **Symptom** | `Android - assembleRelease` fails: APK hashes differ between two clean `assembleRelease` runs on CI |
| **Cause** | Usually a reproducibility regression (non-hermetic timestamp, path, or dependency drift). Rare runner flakes are possible but treated as failures to catch real regressions early |
| **Fix** | Rebuild locally with `SOURCE_DATE_EPOCH=1700000000 ./gradlew clean assembleRelease` twice; compare `sha256sum` of release APK. Align `build.gradle.kts`, `gradle.properties`, and dependency lockfiles with `modules/android/MODULE.md` |
| **Prevention** | Keep `SOURCE_DATE_EPOCH` pinned in CI; use `scripts/verify-reproducible-apk.sh --strict` before release tags. Do not downgrade the job to WARN — strict compare is intentional (M17 P2) |

### KB-010 — Concurrent BLE scan: OBD Classic + IMU + TPMS (Sprint 5b)

| Field | Detail |
|-------|--------|
| **Symptom** | TPMS or IMU advertisements missed while OBD Classic SPP is active; scan appears intermittent |
| **Cause** | Android shares one `BluetoothLeScanner` callback via `BleScanCoordinator`; Classic BT (OBD on ELM327) competes for radio time on some chipsets (validated OnePlus 12) |
| **Fix** | Demux all BLE scan results in `BleScanCoordinator`; TPMS is advertisement-only (no GATT connections); IMU capped at 4 GATT links via `BleConnectionBudget`; OBD uses separate `ClassicBluetoothBudget` |
| **Prevention** | Start TPMS scan on record when enabled; assign corners before session; expect ~1 Hz TPMS updates — sufficient for logging at 20 Hz with last-known merge. Document hardware matrix in `docs/COMPATIBLE_HARDWARE.md` |

### KB-011 — External GPS + OBD + BLE concurrent use (Sprint 5c)

| Field | Detail |
|-------|--------|
| **Symptom** | External NMEA fix stalls when OBD SPP reconnects; HUD flips EXTERNAL → PHONE intermittently |
| **Cause** | `ClassicBluetoothBudget` allows one OBD + one external GPS SPP; reconnect storms or stale NMEA (>2 s) trigger phone fallback in `FusedGpsLocationProvider` |
| **Fix** | Route phone fixes through fusion (`PhoneGpsProvider` callback); prefer external when `NmeaFix.valid` and fresh; log via `ExpeditionGauge/Gps` tag for ADB validation |
| **Prevention** | Enable external GPS in Settings before session; pair GLO 2 / Dual XGPS in system BT first; expect phone fallback under cover or when external receiver sleeps |

### KB-012 — Phone-only 20-min thermal baseline (Sprint 8)

| Field | Detail |
|-------|--------|
| **Symptom** | Need confidence that core v1 recording is thermally safe on phone-only path before F-Droid ship |
| **Cause** | 30 s `thermal-recording` ADB smoke is regression-only; does not stress CPU/GPS for extended sessions |
| **Fix** | Manual 20 min phone-only recording at default 50 Hz; log `dumpsys thermalservice` every 5 min; note HUD thermal banner |
| **Prevention** | Lower log rate in Settings (20→10→5 Hz); connect external IMU to offload phone sensors; see `docs/THERMAL_PERFORMANCE.md` |

### KB-013 — Gitignored app-update assets + FileProvider scope (Audit 2026-06-30)

| Field | Detail |
|-------|--------|
| **Symptom** | In-app update check always null; CI FOSS/FileProvider audit flags broad cache exposure |
| **Cause** | `app-update.json` is gitignored; fresh clones ship placeholder `OWNER/REPO`. `file_paths.xml` had `cache_root` path `.` exposing all cache files to share intents |
| **Fix** | Run `bash scripts/sync-app-update-from-config.sh` (also wired in CI before Android builds/tests). Removed `cache_root`; route burn-in MP4 to `cache/exports/` |
| **Prevention** | Keep `project.config.json` `releaseRepo` as `github.com/owner/repo`; CI sync step must run before Gradle. FileProvider paths: `updates/`, `exports/`, `files/sessions/` only |

### KB-014 — AGP test-harness Netty CVEs in Trivy (Audit 2026-06-30)

| Field | Detail |
|-------|--------|
| **Symptom** | Security Scan fails with 10+ HIGH CVEs on `io.netty:*` in `examples/android/app/gradle.lockfile` |
| **Cause** | Android Gradle Plugin `unified-test-platform-*` pulls Netty 4.1.110/4.1.93 for emulator/test plugins — not packaged in release APK |
| **Fix** | Root `.trivyignore` lists affected CVE IDs; revisit when AGP bumps transitive Netty |
| **Prevention** | Do not ignore Netty in runtime classpath; prefer AGP upgrade over blanket ignore when fix version ships |

### KB-015 — BouncyCastle CVE-2025-14813 in lockfile (Ship 2026-06-30)

| Field | Detail |
|-------|--------|
| **Symptom** | Security Scan fails on `org.bouncycastle:bcprov-jdk18on:1.78.1` / `1.79` in `gradle.lockfile` after MapLibre / Robolectric deps |
| **Cause** | Robolectric and AGP `unified-test-platform-*` declare older BouncyCastle; dependency locking records plugin pins separately from resolved runtime |
| **Fix** | Explicit `testImplementation` on `bcprov-jdk18on:1.80.2` for unit-test runtime; `.trivyignore` for AGP `unified-test-platform` lockfile pins (do not `force` globally — breaks locked androidTest plugin) |
| **Prevention** | Re-run `./gradlew --write-locks` after dependency changes; prefer targeted test deps over `resolutionStrategy.force` on all configurations |

### KB-016 — Child-repo release tag vs `.template-version` (Ship 2026-07-02)

| Field | Detail |
|-------|--------|
| **Symptom** | `Release` workflow SBOM job fails: `Tag v2.12.0 does not match .template-version (0.11.1)` |
| **Cause** | ExpeditionGauge app semver (`versionName`) diverges from upstream template `.template-version` |
| **Fix** | `release.yml` accepts tag when it matches `examples/android/app/build.gradle.kts` `versionName`; backfill via `workflow_dispatch` + `tag` input |
| **Prevention** | After publishing app release, run **Actions → Release → Run workflow** with `tag=vX.Y.Z` if `release` event SBOM step failed |

### KB-017 — Cursor agent shell opens scripts and steals focus (Quiet Agent Shell)

| Field | Detail |
|-------|--------|
| **Symptom** | Agent runs `bash scripts/foo.sh`; Cursor opens hook or script tabs; keystrokes land in wrong editor tab |
| **Cause** | `beforeShellExecution` hooks and command strings containing `.sh` paths trigger editor reveal |
| **Fix** | Python hooks in `.cursor/hooks/*.py`; agent commands via `python3 scripts/agent-run.py <name>`; `.vscode/settings.json` disables auto-reveal |
| **Prevention** | Keep agent-facing docs in `.cursor/` on `agent-run.py`; pin active tab during agent sessions; optional `<!-- cursor-hooks: off -->` in `BUILD_PLAN.md` |

### KB-018 — Weekly-health 0-job failure on push (Ship 2026-07-09)

| Field | Detail |
|-------|--------|
| **Symptom** | `weekly-health-check.yml` concludes **failure** on every `main` push with **0 jobs** |
| **Cause** | Path-filter / `if:` on all jobs can yield an empty matrix; GitHub marks the run failed. A push stub job still failed with 0 jobs on the introducing commit. |
| **Fix** | Remove `on.push` entirely — run only on `schedule` (Monday 07:00 UTC) and `workflow_dispatch`. |
| **Prevention** | Do not re-add `on.push` to weekly-health without a guaranteed always-run job that GitHub actually schedules. |

### KB-019 — Inclinometer landscape after portrait Zero (ADR-0013)

| Field | Detail |
|-------|--------|
| **Symptom** | After Zero in portrait, rotate 90° CCW → aviation horizon vertical (sky left / ground right) |
| **Cause** | (1) Application `WindowManager` reports `ROTATION_0` while Activity is `ROTATION_90` and overwrites fusion; (2) post-Madgwick Euler unwrap is gimbal-fragile |
| **Fix** | `SensorAxisRemap` before Madgwick; Activity `Display.rotation` authoritative; Madgwick reset on rotation change; locked portrait pitch↔roll swap unchanged |
| **Prevention** | `.cursor/rules/inclinometer-rotation.mdc`; `SensorAxisRemapTest`; do not reintroduce Application WM on gyro path or Euler unwrap as primary fix |

### KB-020 — CHANGELOG must keep `## [Unreleased]`

| Field | Detail |
|-------|--------|
| **Symptom** | CI `Validate Bootstrap Artifacts` / upgrade simulation fail: `CHANGELOG.md must have exactly one ## [Unreleased] section (found 0)` |
| **Cause** | Shipping a version section and deleting the empty Unreleased heading |
| **Fix** | Always leave a blank `## [Unreleased]` above the latest versioned section |
| **Prevention** | Run `validate-bootstrap --quick` before `/ship`; never remove Unreleased when cutting a release |

### KB-021 — Pre-release gate stack from `project.config.json`

| Field | Detail |
|-------|--------|
| **Symptom** | Local `pre-release-gate` fails `environment` / `go not found` on ExpeditionGauge despite `stack=android` |
| **Cause** | Gate hardcoded `--stack multi --strict`, which blocks missing Go/Rust toolchains |
| **Fix** | `scripts/pre-release-gate.sh` reads `project.config.json` `stack` (android) |
| **Prevention** | Child repos keep accurate `stack` in project.config; do not force multi on android-only machines |

### KB-022 — Dependabot alert count must not use `?page=`

| Field | Detail |
|-------|--------|
| **Symptom** | `pre-release-gate` / `security-triage --strict` warns “could not fetch Dependabot alerts” and fails |
| **Cause** | `gh api … -f page=` / `?page=` — Dependabot alerts API rejects page pagination (HTTP 400) |
| **Fix** | `scripts/count-critical-high-dependabot.sh` uses `gh api --paginate` with query `state=open&per_page=100` only |
| **Prevention** | Do not use `-f` form fields for GET Dependabot alerts |

### KB-023 — `file://` git clone under Git Bash needs Windows path

| Field | Detail |
|-------|--------|
| **Symptom** | `simulate-template-upgrade` fails: `'/c/Users/…' does not appear to be a git repository` |
| **Cause** | Native `git` cannot use MSYS `file:///c/Users/...` URLs |
| **Fix** | Convert ROOT with `cygpath -m` (or `/c/…` → `C:/…`) before `git clone file://…`; use Windows path for clone destination (MSYS `/tmp` + native git can no-op) |
| **Prevention** | Prefer relative paths after `cd` for Python; convert absolute MSYS paths before native Windows tools |

### KB-024 — Upgrade-sim init smoke missing `.template-update.json` (Windows /ship)

| Field | Detail |
|-------|--------|
| **Symptom** | Local `simulate-template-upgrade` fails after bootstrap quick: `FileNotFoundError: …/child/.template-update.json` during non-interactive web init smoke |
| **Cause** | Init smoke path expects template-update sidecar written by prune/init; Windows `/tmp` clone + web-stack init does not always emit the file in ExpeditionGauge child context |
| **Fix** | Treat as non-blocking for android-only `/ship` when CI `CI` + Security + CodeQL are green; re-run under Git Bash after `cygpath` clone fix (KB-023) or skip web init smoke for `stack=android` |
| **Prevention** | Prefer CI upgrade job / android feature-gate for child-repo release regression; do not fail `/regress` solely on local web init smoke |

### KB-025 — create-release.ps1 nested bash loses JAVA_HOME/gh (Ship 2026-07-19)

| Field | Detail |
|-------|--------|
| **Symptom** | create-release.ps1 fails pre-release gate: JAVA_HOME not set; Android gate skipped and gh CLI required even when set in the parent PowerShell session |
| **Cause** | Gate runs via bash scripts/pre-release-gate.sh from a nested pwsh that does not reliably inherit Windows env / PATH for Git Bash |
| **Fix** | Prefer `python3 scripts/agent-run.py pre-release-gate` (primary path in `pre-release-gate.ps1`). Fallback here-string must use `${prefixColon}:`$PATH` (not `$prefixColon:`) or PowerShell parses a drive-scoped variable and aborts before bash runs (fixed 2026-07-21). Manual: assembleRelease → sign-release-apk.ps1 → pack-aa-install-kit.ps1 → gh release create |
| **Prevention** | Keep agent-run as the Windows gate entry; add a smoke test that parses `pre-release-gate.ps1` under pwsh |

### KB-026 — AGP 9.3.x / Kotlin 2.4.10 Dependabot breaks Android CI (Ship 2026-07-21; re-hit 2026-07-28)

| Field | Detail |
|-------|--------|
| **Symptom** | After Dependabot #11 / #13, CI fails `:app:processDebugNavigationResources` / CodeQL reports Kotlin version too new; Security Scan also fails on new AGP-test Netty CVEs |
| **Cause** | AGP 9.3.x + Kotlin 2.4.10 not yet compatible with this project's Gradle/CodeQL bundle; Trivy DB adds HIGH Netty CVEs on `unified-test-platform` lockfile pins |
| **Fix** | Revert plugins to AGP 9.2.1 / Kotlin 2.4.0 (`examples/android/build.gradle.kts`); append new Netty CVE IDs to `.trivyignore` (KB-014) |
| **Prevention** | Hold android-dependencies group until AGP 9.3 + CodeQL extractor are validated locally and in CI; disable automerge for that group if needed |


### KB-027 — OBDex DTC asset over 500 KB hygiene gate (Ship 2026-07-28)

| Field | Detail |
|-------|--------|
| **Symptom** | CI Repo Hygiene / Feature Gate fail: obdex_en.json 545 KB > 500 KB |
| **Cause** | Full OBDex English title map is ~560 KB JSON; local pre-release missed it while untracked (large-file check uses HEAD sizes) |
| **Fix** | Store ssets/dtc/obdex_en.gz (~70 KB). aapt decompresses .gz and strips the extension — runtime open dtc/obdex_en as plain JSON |
| **Prevention** | Do not commit uncompressed catalogs over 500 KB; regen via etch-obdex-dtc.py writes .gz; avoid .json.gz filename (merger clashes with .json) |

