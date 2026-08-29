# Feature: live-encrypt

> Optional passphrase XOR-seal for live metric payloads.

## Acceptance criteria

- ✅ Blank key leaves payload plain
- ✅ `enc|` hex opens only with the same key
- ✅ Sender seals after encode; receiver opens after unwrap
- ✅ i18n: `live_encrypt_key`

## Smoke scenario

1. Given a live passphrase
2. When a metric is sent
3. Then the receiver decrypts the same JSON

## Container map

| Layer | Path |
|-------|------|
| Logic | `liveencrypt/LiveEncrypt.kt` |
| Tests | `app/src/test/.../liveencrypt/` |
| Wiring | `LiveTelemetrySender`, `LiveTelemetryReceiver` |

## Tests

- Automated: yes — `LiveEncryptTest`
- Coverage: round-trip; missing key

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
