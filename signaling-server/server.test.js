"use strict";
const { test } = require("node:test");
const assert = require("node:assert/strict");
const crypto = require("crypto");
const net = require("net");
const { createServer } = require("./server");
const { parseFrames, MAX_PAYLOAD } = require("./ws-lite");

function connect(url) {
  assert.equal(typeof WebSocket, "function");
  return new WebSocket(url);
}

function nextType(ws, type) {
  return new Promise((resolve, reject) => {
    const onMsg = (ev) => {
      const msg = JSON.parse(String(ev.data));
      if (msg.type !== type) return;
      ws.removeEventListener("message", onMsg);
      resolve(msg);
    };
    ws.addEventListener("message", onMsg);
    ws.addEventListener("error", reject, { once: true });
  });
}

test("join relays metric to receiver", async () => {
  const server = createServer();
  await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
  const { port } = server.address();
  const url = `ws://127.0.0.1:${port}/live`;
  const sender = connect(url);
  const receiver = connect(url);
  await Promise.all([
    new Promise((r) => sender.addEventListener("open", r, { once: true })),
    new Promise((r) => receiver.addEventListener("open", r, { once: true })),
  ]);
  const receiverJoined = nextType(receiver, "joined");
  const receiverMetric = nextType(receiver, "metric");
  sender.send(JSON.stringify({ type: "join", sessionId: "s1", code: "123456", role: "sender" }));
  receiver.send(JSON.stringify({ type: "join", sessionId: "s1", code: "123456", role: "receiver" }));
  const joined = await receiverJoined;
  assert.equal(joined.type, "joined");
  sender.send(JSON.stringify({ type: "metric", payload: "{\"rpm\":1}" }));
  const metric = await receiverMetric;
  assert.equal(metric.type, "metric");
  assert.equal(metric.payload, "{\"rpm\":1}");
  sender.close();
  receiver.close();
  await new Promise((resolve) => server.close(resolve));
});

test("parseFrames rejects advertised oversize payload before buffering it", () => {
  const hdr = Buffer.alloc(10);
  hdr[0] = 0x81;
  hdr[1] = 127;
  hdr.writeBigUInt64BE(BigInt(MAX_PAYLOAD) + 1n, 2);
  const parsed = parseFrames(hdr);
  assert.equal(parsed.error, "too_big");
  assert.equal(parsed.rest.length, 0);
});

test("closes the socket when a client advertises a huge frame", async () => {
  const server = createServer();
  await new Promise((resolve) => server.listen(0, "127.0.0.1", resolve));
  const { port } = server.address();
  const socket = net.connect(port, "127.0.0.1");
  const key = crypto.randomBytes(16).toString("base64");
  await new Promise((resolve, reject) => {
    socket.once("error", reject);
    socket.once("connect", resolve);
  });
  socket.write(
    "GET /live HTTP/1.1\r\nHost: 127.0.0.1\r\nUpgrade: websocket\r\n" +
      `Connection: Upgrade\r\nSec-WebSocket-Key: ${key}\r\nSec-WebSocket-Version: 13\r\n\r\n`,
  );
  await new Promise((resolve) => socket.once("data", resolve));
  const hdr = Buffer.alloc(10);
  hdr[0] = 0x81;
  hdr[1] = 127;
  hdr.writeBigUInt64BE(10n ** 12n, 2);
  socket.write(hdr);
  await new Promise((resolve) => socket.once("close", resolve));
  await new Promise((resolve) => server.close(resolve));
});
