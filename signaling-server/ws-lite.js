"use strict";
const crypto = require("crypto");
const GUID = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";

function acceptKey(key) {
  return crypto.createHash("sha1").update(String(key) + GUID).digest("base64");
}

function encode(data, opcode = 1, mask = false) {
  const payload = Buffer.isBuffer(data) ? data : Buffer.from(String(data));
  const len = payload.length;
  let hdr;
  if (len < 126) {
    hdr = Buffer.alloc(2);
    hdr[1] = (mask ? 0x80 : 0) | len;
  } else if (len < 65536) {
    hdr = Buffer.alloc(4);
    hdr[1] = (mask ? 0x80 : 0) | 126;
    hdr.writeUInt16BE(len, 2);
  } else {
    hdr = Buffer.alloc(10);
    hdr[1] = (mask ? 0x80 : 0) | 127;
    hdr.writeBigUInt64BE(BigInt(len), 2);
  }
  hdr[0] = 0x80 | opcode;
  if (!mask) return Buffer.concat([hdr, payload]);
  const key = crypto.randomBytes(4);
  const masked = Buffer.alloc(len);
  for (let i = 0; i < len; i++) masked[i] = payload[i] ^ key[i & 3];
  return Buffer.concat([hdr, key, masked]);
}

function parseFrames(buf) {
  const frames = [];
  let offset = 0;
  while (buf.length - offset >= 2) {
    const b1 = buf[offset + 1];
    const masked = (b1 & 0x80) !== 0;
    let len = b1 & 0x7f;
    let hdr = 2;
    if (len === 126) {
      if (buf.length - offset < 4) break;
      len = buf.readUInt16BE(offset + 2);
      hdr = 4;
    } else if (len === 127) {
      if (buf.length - offset < 10) break;
      len = Number(buf.readBigUInt64BE(offset + 2));
      hdr = 10;
    }
    const maskLen = masked ? 4 : 0;
    if (buf.length - offset < hdr + maskLen + len) break;
    let payload = buf.subarray(offset + hdr + maskLen, offset + hdr + maskLen + len);
    if (masked) {
      const mask = buf.subarray(offset + hdr, offset + hdr + 4);
      const out = Buffer.alloc(payload.length);
      for (let i = 0; i < payload.length; i++) out[i] = payload[i] ^ mask[i & 3];
      payload = out;
    }
    frames.push({ fin: (buf[offset] & 0x80) !== 0, opcode: buf[offset] & 0x0f, payload });
    offset += hdr + maskLen + len;
  }
  return { frames, rest: buf.subarray(offset) };
}

class WsSocket {
  constructor(socket) {
    this._socket = socket;
    this.readyState = 1;
    this.OPEN = 1;
    this._buf = Buffer.alloc(0);
    this._parts = [];
    this._opcode = 0;
    this._handlers = { message: [], close: [] };
    socket.on("data", (chunk) => this._onData(chunk));
    socket.on("close", () => this._close());
    socket.on("error", () => this._close());
  }

  on(ev, fn) {
    (this._handlers[ev] || (this._handlers[ev] = [])).push(fn);
  }

  send(data) {
    if (this.readyState !== 1) return;
    this._socket.write(encode(data, 1, false));
  }

  _onData(chunk) {
    this._buf = Buffer.concat([this._buf, chunk]);
    const parsed = parseFrames(this._buf);
    this._buf = parsed.rest;
    for (const frame of parsed.frames) this._handle(frame);
  }

  _handle(frame) {
    if (frame.opcode === 8) {
      this._close();
      return;
    }
    if (frame.opcode === 9) {
      if (this.readyState === 1) this._socket.write(encode(frame.payload, 10, false));
      return;
    }
    if (frame.opcode === 10) return;
    if (frame.opcode === 1 || frame.opcode === 2) {
      this._opcode = frame.opcode;
      this._parts = [frame.payload];
    } else if (frame.opcode === 0) {
      this._parts.push(frame.payload);
    } else {
      return;
    }
    if (!frame.fin) return;
    const payload = Buffer.concat(this._parts);
    this._parts = [];
    const msg = this._opcode === 1 ? payload.toString("utf8") : payload;
    for (const fn of this._handlers.message) fn(msg);
  }

  _close() {
    if (this.readyState === 3) return;
    this.readyState = 3;
    try { this._socket.end(); } catch { /* ignore */ }
    for (const fn of this._handlers.close) fn();
  }
}

function attachWebSocket(server, { path, onConnection }) {
  server.on("upgrade", (req, socket, head) => {
    const urlPath = String(req.url || "").split("?")[0];
    if (urlPath !== path) {
      socket.write("HTTP/1.1 404 Not Found\r\nConnection: close\r\n\r\n");
      socket.destroy();
      return;
    }
    const key = req.headers["sec-websocket-key"];
    if (!key) {
      socket.destroy();
      return;
    }
    socket.write(
      "HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\n" +
        `Sec-WebSocket-Accept: ${acceptKey(key)}\r\n\r\n`,
    );
    if (head && head.length) socket.unshift(head);
    onConnection(new WsSocket(socket));
  });
}

module.exports = { attachWebSocket, encode, acceptKey, WsSocket };
