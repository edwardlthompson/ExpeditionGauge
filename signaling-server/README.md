# ExpeditionGauge signaling server

Minimal FOSS WebSocket room server for Live Telemetry (Sprint 19).

**Not part of the Android APK.** Node stdlib + `ws-lite.js` only (no npm packages, no native addons) so F-Droid's scanner can leave this tree in place.

## Run locally

```bash
cd signaling-server
node server.js
# or: npm start
```

Default: `ws://127.0.0.1:8787/live`

Set in app **Settings → Live telemetry → Signaling URL** (or `project.config.json` dev default).

```bash
npm test
```

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
