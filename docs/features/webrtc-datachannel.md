# Feature: webrtc-datachannel

> Frame live metrics as a FOSS Data Channel payload (ADR-0006) without a proprietary SDK.

## Acceptance criteria

- ✅ Payloads wrap with `dc1|`
- ✅ Unwrap accepts both framed and plain JSON
- ✅ Channel opens New → Connecting → Open
- ✅ Sender wraps; receiver unwraps
- ✅ i18n: none

## Smoke scenario

1. Given a live metric JSON
2. When the sender transmits
3. Then the receiver unwraps `dc1|` and parses the sample

## Container map

| Layer | Path |
|-------|------|
| Logic | `webrtcdatachannel/WebRtcDataChannel.kt` |
| Tests | `app/src/test/.../webrtcdatachannel/` |
| Wiring | `LiveTelemetrySender`, `LiveTelemetryReceiver` |

## Tests

- Automated: yes — `WebRtcDataChannelTest`
- Coverage: wrap/unwrap; state

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
