"use strict";
const { test } = require("node:test");
const assert = require("node:assert/strict");
const { createServer } = require("./server");

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
