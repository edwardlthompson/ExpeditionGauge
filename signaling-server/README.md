# ExpeditionGauge signaling server

Minimal FOSS WebSocket room server for Live Telemetry (Sprint 19).

## Run locally

```bash
cd signaling-server
npm install
npm start
```

Default: `ws://127.0.0.1:8787/live`

Set in app **Settings → Live telemetry → Signaling URL** (or `project.config.json` dev default).

## Protocol

| Message | Direction | Purpose |
|---------|-----------|---------|
| `join` | client → server | `{ sessionId, code, role: sender\|receiver }` |
| `joined` | server → client | `{ receiverCount }` |
| `metric` | sender → receivers | `{ payload: "<json>" }` stub relay until WebRTC P2P |
| `sdp` / `ice` | bidirectional | WebRTC handshake relay (no metric storage) |

## Privacy

Server stores only ephemeral room membership. Metric payloads are forwarded in-room only (stub transport); production target is WebRTC Data Channel P2P per ADR-0006.

## Self-host

Deploy behind HTTPS/WSS reverse proxy (nginx, Caddy). Document your URL in `docs/features/live-telemetry.md`.
