# Feature: live-multi-receiver

> One live sender can feed up to eight pit-room receivers.

## Acceptance criteria

- ✅ Counts 0–8 are accepted
- ✅ Fan-out clones the payload up to the cap
- ✅ Settings live section shows `n / 8`
- ✅ i18n: `live_multi_receiver`

## Smoke scenario

1. Given three connected receivers
2. When a metric is sent
3. Then fan-out yields three copies

## Container map

| Layer | Path |
|-------|------|
| Logic | `livemultireceiver/LiveMultiReceiver.kt` |
| Tests | `app/src/test/.../livemultireceiver/` |
| Wiring | `LiveTelemetryModule.receiverCount` label |

## Tests

- Automated: yes — `LiveMultiReceiverTest`
- Coverage: accept; cap

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
