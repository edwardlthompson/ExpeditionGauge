# Click ExpeditionGauge open on a running Desktop Head Unit window (Windows).
# Uses PostMessage client coords (mouse_event alone is flaky against DHU).
param()

$ErrorActionPreference = "Stop"

$cs = @'
using System;
using System.Runtime.InteropServices;
public static class DhuTapPm {
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern bool GetClientRect(IntPtr hWnd, out RECT r);
  [DllImport("user32.dll")] public static extern bool PostMessage(IntPtr hWnd, uint Msg, IntPtr wParam, IntPtr lParam);
  [DllImport("user32.dll")] public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, UIntPtr dwExtraInfo);
  public struct RECT { public int Left; public int Top; public int Right; public int Bottom; }
  const uint WM_LBUTTONDOWN = 0x0201;
  const uint WM_LBUTTONUP = 0x0202;
  const uint KEYUP = 0x0002;
  static IntPtr LParam(int x, int y) { return (IntPtr)((y << 16) | (x & 0xFFFF)); }
  public static void Esc(IntPtr hwnd) {
    ShowWindow(hwnd, 9);
    SetForegroundWindow(hwnd);
    System.Threading.Thread.Sleep(120);
    keybd_event(0x1B, 0, 0, UIntPtr.Zero);
    keybd_event(0x1B, 0, KEYUP, UIntPtr.Zero);
  }
  public static void ClickClient(IntPtr hwnd, double fx, double fy) {
    ShowWindow(hwnd, 9);
    SetForegroundWindow(hwnd);
    System.Threading.Thread.Sleep(120);
    RECT cr;
    GetClientRect(hwnd, out cr);
    int x = (int)(cr.Right * fx);
    int y = (int)(cr.Bottom * fy);
    IntPtr lp = LParam(x, y);
    PostMessage(hwnd, WM_LBUTTONDOWN, (IntPtr)1, lp);
    System.Threading.Thread.Sleep(80);
    PostMessage(hwnd, WM_LBUTTONUP, IntPtr.Zero, lp);
  }
}
'@
Add-Type -TypeDefinition $cs

$p = Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue |
    Where-Object { $_.MainWindowHandle -ne [IntPtr]::Zero } |
    Select-Object -First 1
if (-not $p) {
    Write-Error "dhu-open-expeditiongauge: DHU window not found"
}

$h = $p.MainWindowHandle

# Dismiss suggestion cards.
1..3 | ForEach-Object { [DhuTapPm]::Esc($h); Start-Sleep -Milliseconds 200 }
Start-Sleep -Milliseconds 350

# App drawer (bottom-left grid) → ExpeditionGauge (row 2, col 3 on typical POI drawer).
[DhuTapPm]::ClickClient($h, 0.05, 0.93)
Start-Sleep -Seconds 2.0
[DhuTapPm]::ClickClient($h, 0.60, 0.48)
Start-Sleep -Seconds 1.5

# Dock fallback: 2nd of 4 recent apps (Waze, EG, Phone, Maps) on Coolwalk.
[DhuTapPm]::ClickClient($h, 0.40, 0.90)
Start-Sleep -Seconds 2.0

Write-Host "OK  Opened ExpeditionGauge (drawer + dock PostMessage)" -ForegroundColor Green
