# Privacy Policy — ExpeditionGauge

ExpeditionGauge is a local-first off-road telemetry app. No account is required. Network access is used only for features you enable.

## Data stored on device

| Data | Purpose | Retention |
|------|---------|-----------|
| GPS tracks, IMU samples, OBD readings | Recording and playback | Until you delete the session |
| Session metadata (name, activity type, notes) | Library organization | Until you delete the session |
| Photos/videos attached to sessions | Scrubber markers and flyover waypoints | Until you delete the session or media |
| Calibration and HUD preferences | App settings | Until app uninstall or reset |
| BLE device pairings (IMU, TPMS, external GPS, OBD) | Sensor connections | Until you forget the device or uninstall |

All session data stays on your device by default. Exports (GPX, video, share cards) are written only when you request them.

## Optional network features

| Feature | Default | Data sent |
|---------|---------|-----------|
| App update check | Opt-in (Settings) | `last_checked`, installed artifact format, release manifest URL — no PII |
| Live telemetry (sender/receiver) | Off | P2P session payloads between paired devices you connect; no cloud relay |

## Data we do not collect

- No analytics or crash reporting unless you explicitly opt in (none shipped by default)
- No sale of personal data
- No PII in logs without your consent
- No Google Play Services or Firebase telemetry in the APK

## Android backup

`android:allowBackup="false"` — session files and calibration are not included in Android cloud backup.

## User rights (GDPR / CCPA)

- **Access:** Session data is visible in-app; export via GPX or share flows where supported
- **Deletion:** Delete individual sessions or uninstall the app
- **Opt-out:** Live telemetry and update checks are off until you enable them
- **Portability:** GPX and exported video files

## Contact

Privacy inquiries: see maintainers in `.github/CODEOWNERS` or [`SECURITY.md`](../SECURITY.md).
