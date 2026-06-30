# Live receiver (Sprint 19)

Static pit-crew dashboard for ExpeditionGauge live telemetry.

## Local use

1. Start signaling server: `cd ../signaling-server && npm start`
2. Serve this folder: `npx serve .` or open `index.html` via a local HTTP server
3. Join with session ID + 6-digit code from the driver app QR

## GitHub Pages

Deploy the `live-receiver/` directory to GitHub Pages. Pass query params `sessionId`, `code`, and `signalWss` for deep links from QR.

## Privacy

Metrics flow through your chosen signaling server during the stub transport phase. Use self-hosted `wss://` in production.
