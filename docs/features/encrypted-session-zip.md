# Feature: encrypted-session-zip

> XOR-seal a session file listing for local ZIP export (no proprietary crypto).

## Acceptance criteria

- ✅ Listing seals with `zip|` prefix
- ✅ Opening with the same key restores names
- ✅ Wrong key or foreign payload fails closed
- ✅ i18n: silent helper (share uses existing export strings)

## Smoke scenario

1. Given session files `run.csv` and `run.gpx`
2. When the listing is sealed with a passphrase
3. Then only that passphrase restores the names

## Container map

| Layer | Path |
|-------|------|
| Logic | `encryptedsessionzip/EncryptedSessionZip.kt` |
| Tests | `app/src/test/.../encryptedsessionzip/` |
| Wiring | `ShareExportLauncher` listing helper |

## Tests

- Automated: yes — `EncryptedSessionZipTest`
- Coverage: seal/open; wrong key

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
