# Third-Party Licenses

> Generated and maintained per release. See pre-release gate in `docs/INITIALIZATION_PROMPT.md` Section 7a.

## Project License

This project is licensed under the MIT License. See [`LICENSE`](LICENSE).

## Dependencies

Run license audits for active stacks:

```bash
# Web (npm)
cd examples/web && npx license-checker --production --summary

# Python (pip)
cd examples/python && uv run pip-licenses --format=markdown

# Rust / Go (optional stacks — MIT stubs; expand when deps are added)
grep 'license' examples/rust/Cargo.toml
head -1 examples/go/go.mod

```

`[AUTO]` CI runs `scripts/check-license-compliance.sh` on each push.

## Android (ExpeditionGauge)

| Dependency | Version | License | Notes |
|------------|---------|---------|-------|
| AndroidX Room | 2.7.2 | Apache-2.0 | Session/sample persistence |
| kotlin-obd-api (JitPack) | 1.4.1 | Apache-2.0 | OBD-II ELM327; pinned tag `1.4.1` |
| MapLibre GL Android SDK | 13.0.2 | BSD-2-Clause | Via MapLibre Compose playback map |
| MapLibre Compose | 0.13.0 | BSD-2-Clause | Playback map (Sprint 7+) |
Lockfile: `examples/android/app/gradle.lockfile` (regenerate with `./gradlew :app:assembleDebug --write-locks`).

## Bundled data (assets)

| Project | License | Use in ExpeditionGauge |
|---------|---------|------------------------|
| [foerbsnavi/OBDex](https://github.com/foerbsnavi/OBDex) | CC0-1.0 (data) | Slim English DTC titles in `assets/dtc/obdex_en.gz` (same catalog [OBDForge](https://github.com/edwardlthompson/OBDForge) uses). Regen: `pwsh scripts/expedition/fetch-obdex-dtc.ps1`. **No** OBDForge / GPL sources are vendored. |
## Reference implementations (not bundled)

| Project | License | Use in ExpeditionGauge |
|---------|---------|------------------------|
| [omadon/TPMS_BLE_BR](https://github.com/omadon/TPMS_BLE_BR) | MIT | Protocol reference for `BrTpmsParser` (Sprint 5b); not a runtime dependency |
| [KreAch3R/tpms-oap](https://github.com/KreAch3R/tpms-oap) | GPL-3.0 | PECHAM GATT protocol reference for v2 `PechamTpmsParser` stub only; not linked in APK |
| [edwardlthompson/OBDForge](https://github.com/edwardlthompson/OBDForge) | GPL-3.0-or-later | Catalog reference only (OBDex CC0 data); not linked or copied into the APK |
## Attribution

When bundling dependencies in releases (APK, desktop binary, etc.), include
this file or a generated `NOTICE` file in the distribution artifact.

## Incompatible Licenses

`[HUMAN]` must approve any dependency with copyleft licenses (GPL, AGPL) that
may affect distribution. Document exceptions in `DECISION_LOG.md`.
