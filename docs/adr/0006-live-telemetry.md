# ADR-0006: Live Telemetry via WebRTC Data Channels

- **Status:** Accepted
- **Date:** 2026-06-29
- **Deciders:** ExpeditionGauge team

## Context

Track-day pit crews need sub-30-second pairing to view driver metrics on a second device. Feature must be opt-in, FOSS-only, and must not fork fusion logic.

## Decision

1. **`FeatureFlags.liveTelemetryEnabled`** defaults to **false** — zero network code until user enables in Settings.
2. **Sender** subscribes to existing **`TelemetryBus`**; **`LiveTelemetryEncoder`** downsamples to 5–10 Hz JSON.
3. **Transport:** WebRTC Data Channel (FOSS stack when wired); **`SignalingClient`** WebSocket stub relays SDP/ICE only — no metric payloads on server.
4. **Pairing:** Ephemeral session id + 6-digit code + QR payload (`expeditiongauge://live?...`).
5. **`live/` package** exposes extension points via **`LiveTelemetryTransport`** for future LAN-only paths.

## Consequences

- Offline recording unaffected when Live is off.
- Signaling server sees join metadata only; metrics are P2P.
- Sprint 19 ships stub PeerConnection architecture; full WebRTC dependency added when audited.

## Alternatives Considered

| Approach | Rejected because |
|----------|------------------|
| Firebase / Google WebRTC SDK | Proprietary; FOSS violation |
| Raw WebSocket metrics stream | No NAT traversal on cellular |
| MQTT cloud broker | Central server stores sensitive telemetry |
