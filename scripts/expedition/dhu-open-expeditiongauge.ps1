# Open ExpeditionGauge on a running Desktop Head Unit.
# Prefers DHU console `tap` (no mouse). Falls back to Windows mouse_event.
param(
    [switch]$MouseFallback
)

$ErrorActionPreference = "Stop"
$Root = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

$p = Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue |
    Where-Object { $_.MainWindowHandle -ne [IntPtr]::Zero } |
    Select-Object -First 1
if (-not $p) {
    Write-Error "dhu-open-expeditiongauge: DHU window not found"
}

# Client size from window (matches DHU touch coords).
Add-Type -TypeDefinition @'
using System;
using System.Runtime.InteropServices;
public static class DhuGeom {
  [DllImport("user32.dll")] public static extern bool GetClientRect(IntPtr h, out RECT r);
  public struct RECT { public int L,T,R,B; }
  public static string Size(IntPtr h) {
    RECT r; GetClientRect(h, out r); return r.R + "x" + r.B;
  }
}
'@
$size = [DhuGeom]::Size($p.MainWindowHandle)
$parts = $size -split 'x'
$w = [int]$parts[0]
$hh = [int]$parts[1]
$tall = $hh -gt $w
Write-Host "dhu-open: client ${w}x${hh} tall=$tall" -ForegroundColor DarkGray

# Touch coords in DHU display pixels (same space as `tap x y`).
$drawerX = [int]($w * 0.06)
$drawerY = [int]($hh * 0.965)
if ($tall) {
    $appX = [int]($w * 0.72)
    $appY = [int]($hh * 0.53)
} else {
    # Landscape 4-col grid: EG is row 2 col 4 (Discord is col 3 at ~0.60).
    $appX = [int]($w * 0.80)
    $appY = [int]($hh * 0.52)
}

function Send-DhuConsole([string]$cmd) {
    & "$PSScriptRoot\dhu-console.ps1" -Command $cmd
    return ($LASTEXITCODE -eq 0)
}

$usedConsole = $false
try {
    # Escape home / dismiss overlays (keycode if available; tap top-left as soft reset)
    if (Send-DhuConsole "sleep 1") {
        $usedConsole = $true
        # Open app drawer, then ExpeditionGauge icon.
        Send-DhuConsole "tap $drawerX $drawerY" | Out-Null
        Send-DhuConsole "sleep 2" | Out-Null
        Send-DhuConsole "tap $appX $appY" | Out-Null
        if (-not $tall) {
            Send-DhuConsole "sleep 1" | Out-Null
            # Dock fallback (landscape)
            $dockX = [int]($w * 0.40)
            $dockY = [int]($hh * 0.90)
            Send-DhuConsole "tap $dockX $dockY" | Out-Null
        }
        Send-DhuConsole "sleep 2" | Out-Null
        Write-Host "OK  Opened ExpeditionGauge (DHU console tap)" -ForegroundColor Green
        exit 0
    }
} catch {
    Write-Warning "dhu-open: console tap unavailable - $($_.Exception.Message)"
}

if (-not $MouseFallback -and -not $usedConsole) {
    Write-Warning "dhu-open: start DHU with dhu-start-controlled.ps1 -RestartDhu for tap-free open; falling back to mouse"
}

# --- mouse_event fallback (legacy) ---
$cs = @'
using System;
using System.Runtime.InteropServices;
public static class DhuTapOpen {
  [DllImport("user32.dll")] public static extern bool SetForegroundWindow(IntPtr hWnd);
  [DllImport("user32.dll")] public static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] public static extern bool GetClientRect(IntPtr hWnd, out RECT r);
  [DllImport("user32.dll")] public static extern bool ClientToScreen(IntPtr hWnd, ref POINT p);
  [DllImport("user32.dll")] public static extern void mouse_event(uint dwFlags, uint dx, uint dy, uint dwData, UIntPtr dwExtraInfo);
  [DllImport("user32.dll")] public static extern bool SetCursorPos(int X, int Y);
  [DllImport("user32.dll")] public static extern void keybd_event(byte bVk, byte bScan, uint dwFlags, UIntPtr dwExtraInfo);
  public struct RECT { public int Left; public int Top; public int Right; public int Bottom; }
  public struct POINT { public int X; public int Y; }
  const uint LEFTDOWN = 0x0002;
  const uint LEFTUP = 0x0004;
  const uint KEYUP = 0x0002;
  public static void Esc() {
    keybd_event(0x1B, 0, 0, UIntPtr.Zero);
    keybd_event(0x1B, 0, KEYUP, UIntPtr.Zero);
  }
  public static void ClickClient(IntPtr hwnd, double fx, double fy) {
    ShowWindow(hwnd, 9);
    SetForegroundWindow(hwnd);
    System.Threading.Thread.Sleep(200);
    RECT cr;
    GetClientRect(hwnd, out cr);
    POINT pt;
    pt.X = (int)(cr.Right * fx);
    pt.Y = (int)(cr.Bottom * fy);
    ClientToScreen(hwnd, ref pt);
    SetCursorPos(pt.X, pt.Y);
    System.Threading.Thread.Sleep(40);
    mouse_event(LEFTDOWN, 0, 0, 0, UIntPtr.Zero);
    System.Threading.Thread.Sleep(50);
    mouse_event(LEFTUP, 0, 0, 0, UIntPtr.Zero);
  }
}
'@
Add-Type -TypeDefinition $cs
$h = $p.MainWindowHandle
1..3 | ForEach-Object { [DhuTapOpen]::Esc(); Start-Sleep -Milliseconds 180 }
Start-Sleep -Milliseconds 350
[DhuTapOpen]::ClickClient($h, 0.06, 0.965)
Start-Sleep -Seconds 2.5
if ($tall) {
    [DhuTapOpen]::ClickClient($h, 0.72, 0.53)
} else {
    [DhuTapOpen]::ClickClient($h, 0.80, 0.52)
    Start-Sleep -Seconds 1.5
    [DhuTapOpen]::ClickClient($h, 0.40, 0.90)
}
Start-Sleep -Seconds 2.0
Write-Host "OK  Opened ExpeditionGauge (mouse fallback)" -ForegroundColor Green
