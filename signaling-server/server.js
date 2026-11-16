#!/usr/bin/env node
/**
 * ExpeditionGauge live signaling + stub metric relay (Sprint 19).
 * Join rooms by sessionId + code. Relays SDP/ICE for WebRTC handshake and
 * metric JSON to receivers in-room (stub transport until P2P WebRTC wired).
 *
 * Zero npm deps (stdlib http + ws-lite) so F-Droid need not scandelete this
 * tree for optional native `ws` peers. Not packaged into the APK.
 */
const http = require("http");
const { attachWebSocket } = require("./ws-lite");

const PORT = Number(process.env.PORT || 8787);
const PATH = process.env.SIGNAL_PATH || "/live";

function send(ws, obj) {
  if (ws.readyState === ws.OPEN) ws.send(JSON.stringify(obj));
}

function receiverCount(room) {
  let n = 0;
  for (const c of room.clients.values()) if (c.role === "receiver") n++;
  return n;
}

function broadcast(room, obj, exceptWs = null) {
  for (const { ws } of room.clients.values()) {
    if (ws !== exceptWs && ws.readyState === ws.OPEN) send(ws, obj);
  }
}

function broadcastReceivers(room, obj) {
  for (const { ws, role } of room.clients.values()) {
    if (role === "receiver" && ws.readyState === ws.OPEN) send(ws, obj);
  }
}

function createServer() {
  /** @type {Map<string, { code: string, clients: Map<object, { role: string, ws: object }> }>} */
  const rooms = new Map();
  const notifyCounts = (sessionId) => {
    const room = rooms.get(sessionId);
    if (!room) return;
    broadcast(room, { type: "joined", receiverCount: receiverCount(room) });
  };
  const server = http.createServer((_req, res) => {
    res.writeHead(200, { "Content-Type": "text/plain" });
    res.end("ExpeditionGauge signaling server OK\n");
  });

  attachWebSocket(server, {
    path: PATH,
    onConnection(ws) {
      let sessionId = null;
      ws.on("message", (raw) => {
        let msg;
        try {
          msg = JSON.parse(String(raw));
        } catch {
          send(ws, { type: "error", message: "invalid json" });
          return;
        }
        if (msg.type === "join") {
          sessionId = msg.sessionId;
          const code = String(msg.code || "");
          const role = msg.role === "receiver" ? "receiver" : "sender";
          if (!sessionId || !code) {
            send(ws, { type: "error", message: "sessionId and code required" });
            return;
          }
          let room = rooms.get(sessionId);
          if (!room) {
            room = { code, clients: new Map() };
            rooms.set(sessionId, room);
          } else if (room.code !== code) {
            send(ws, { type: "error", message: "invalid code" });
            return;
          }
          room.clients.set(ws, { role, ws });
          send(ws, { type: "joined", receiverCount: receiverCount(room), role });
          notifyCounts(sessionId);
          return;
        }
        const room = sessionId ? rooms.get(sessionId) : null;
        if (!room) return;
        if (msg.type === "metric" && typeof msg.payload === "string") {
          broadcastReceivers(room, { type: "metric", payload: msg.payload });
          return;
        }
        if (msg.type === "sdp" || msg.type === "ice") {
          broadcast(room, msg, ws);
        }
      });
      ws.on("close", () => {
        if (!sessionId) return;
        const room = rooms.get(sessionId);
        if (!room) return;
        room.clients.delete(ws);
        if (room.clients.size === 0) rooms.delete(sessionId);
        else notifyCounts(sessionId);
      });
    },
  });
  return server;
}

if (require.main === module) {
  const server = createServer();
  server.listen(PORT, () => {
    console.log(`signaling listening ws://0.0.0.0:${PORT}${PATH}`);
  });
}

module.exports = { createServer };
