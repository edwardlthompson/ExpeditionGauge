(function () {
  const sessionIdEl = document.getElementById("sessionId");
  const codeEl = document.getElementById("code");
  const signalWssEl = document.getElementById("signalWss");
  const joinBtn = document.getElementById("joinBtn");
  const disconnectBtn = document.getElementById("disconnectBtn");
  const statusEl = document.getElementById("status");
  const gaugesEl = document.getElementById("gauges");
  const joinPanel = document.getElementById("join-panel");

  let ws = null;

  function setStatus(msg) {
    statusEl.textContent = msg;
  }

  function parseQrPayload(url) {
    try {
      const u = new URL(url.replace(/^expeditiongauge:\/\//, "https://"));
      return {
        sessionId: u.searchParams.get("sessionId") || "",
        code: u.searchParams.get("code") || "",
        signalWss: decodeURIComponent(u.searchParams.get("signalWss") || ""),
      };
    } catch {
      return null;
    }
  }

  function applyQueryParams() {
    const q = new URLSearchParams(location.search);
    if (q.get("sessionId")) sessionIdEl.value = q.get("sessionId");
    if (q.get("code")) codeEl.value = q.get("code");
    if (q.get("signalWss")) signalWssEl.value = decodeURIComponent(q.get("signalWss"));
    const payload = q.get("payload");
    if (payload) {
      const parsed = parseQrPayload(payload);
      if (parsed) {
        sessionIdEl.value = parsed.sessionId;
        codeEl.value = parsed.code;
        if (parsed.signalWss) signalWssEl.value = parsed.signalWss;
      }
    }
  }

  function updateGauges(sample) {
    document.getElementById("speed").textContent = Math.round(sample.speed * 3.6);
    document.getElementById("latG").textContent = sample.latG.toFixed(2);
    document.getElementById("beta").textContent = sample.beta != null ? Math.round(sample.beta) : "—";
    document.getElementById("pitch").textContent = Math.round(sample.pitch);
    document.getElementById("roll").textContent = Math.round(sample.roll);
  }

  function connect() {
    const sessionId = sessionIdEl.value.trim();
    const code = codeEl.value.trim();
    const url = signalWssEl.value.trim();
    if (!sessionId || !code || !url) {
      setStatus("Session ID, code, and signaling URL required");
      return;
    }
    ws = new WebSocket(url);
    ws.onopen = () => {
      ws.send(JSON.stringify({ type: "join", role: "receiver", sessionId, code }));
      setStatus("Connecting…");
    };
    ws.onmessage = (ev) => {
      let msg;
      try { msg = JSON.parse(ev.data); } catch { return; }
      if (msg.type === "joined") {
        setStatus("Connected — waiting for metrics");
        joinPanel.hidden = true;
        gaugesEl.hidden = false;
      } else if (msg.type === "metric" && msg.payload) {
        try {
          updateGauges(JSON.parse(msg.payload));
        } catch { /* ignore */ }
      } else if (msg.type === "error") {
        setStatus(msg.message || "Error");
      }
    };
    ws.onclose = () => disconnect();
    ws.onerror = () => setStatus("WebSocket error");
  }

  function disconnect() {
    if (ws) {
      ws.close();
      ws = null;
    }
    gaugesEl.hidden = true;
    joinPanel.hidden = false;
    setStatus("Disconnected");
  }

  joinBtn.addEventListener("click", connect);
  disconnectBtn.addEventListener("click", disconnect);
  applyQueryParams();
})();
