# Live Telemetry (Sender / Receiver)

> Sprint 19 — opt-in pit-crew streaming via WebSocket signaling + stub metric relay (ADR-0006).

## Overview

Live Telemetry lets a driver share downsampled gauge metrics with pit crew on a second device or browser. The feature is **disabled by default**; enabling it in Settings is required before any network code runs.

## Architecture

| Component | Role |
|-----------|------|
| `LiveTelemetrySender` | Subscribes to `TelemetryBus`; encodes JSON via `LiveTelemetryEncoder` |
| `LiveWebSocketClient` | OkHttp WebSocket — join room, relay metrics (stub) and SDP/ICE |
| `LiveTelemetryReceiver` | In-app receiver; parses metric JSON into `LiveSampleDto` |
| `LivePairingManager` | Ephemeral UUID session + 6-digit code + QR payload |
| `signaling-server/` | Self-hosted FOSS room server (`ws://host:8787/live`) |
| `live-receiver/` | Static web dashboard (GitHub Pages) |

**Interim transport:** Metrics relay through the signaling WebSocket (`type: metric`) for E2E demos until FOSS WebRTC Data Channel is wired (ADR-0006). Signaling server does not persist payloads.

## Pairing

QR payload format:

```
expeditiongauge://live?v=1&sessionId=<uuid>&code=<6-digit>&signalWss=<url-encoded-wss>
```

Driver taps **Start live session** on the dashboard when Live Telemetry is enabled. Pit crew scans QR or enters session ID + code on the web receiver or in-app **Live receiver** screen.

## Settings

- **Live telemetry (v2)** — master toggle (DataStore `live_telemetry_enabled`)
- **Signaling WebSocket URL** — default `ws://127.0.0.1:8787/live` for local dev; use `wss://` in production

## Privacy

- No cloud analytics; no metric storage on signaling server
- Join metadata only (session id + short code)
- Offline recording unaffected when Live is off

## Self-host signaling

```bash
cd signaling-server
npm install
npm start
# listens on ws://0.0.0.0:8787/live
```

## Web receiver

Deploy `live-receiver/` to GitHub Pages or open locally. Default signaling URL is configurable in the join form.

## TPMS

When TPMS is active, encoded JSON includes a `tpms` object with corner pressure/temp readings.
