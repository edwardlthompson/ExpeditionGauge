# Capture the Desktop Head Unit window to a PNG (Windows).
param(
    [string]$OutFile = "C:\Users\edwar\ExpeditionGauge\.cursor\screenshots\dhu-live.png"
)

$ErrorActionPreference = "Stop"
New-Item -ItemType Directory -Force -Path (Split-Path $OutFile) | Out-Null

Add-Type -AssemblyName System.Drawing
$cs = @'
using System;
using System.Drawing;
using System.Drawing.Imaging;
using System.Runtime.InteropServices;
public static class DhuWindowCapture {
  [DllImport("user32.dll")] static extern bool GetWindowRect(IntPtr hWnd, out RECT lpRect);
  [DllImport("user32.dll")] static extern bool SetForegroundWindow(IntPtr hWnd);
  [DllImport("user32.dll")] static extern bool ShowWindow(IntPtr hWnd, int nCmdShow);
  [DllImport("user32.dll")] static extern bool PrintWindow(IntPtr hWnd, IntPtr hdcBlt, uint nFlags);
  public struct RECT { public int Left; public int Top; public int Right; public int Bottom; }
  const uint PW_RENDERFULLCONTENT = 0x00000002;
  public static void Capture(IntPtr hwnd, string path) {
    ShowWindow(hwnd, 9);
    SetForegroundWindow(hwnd);
    System.Threading.Thread.Sleep(400);
    RECT r;
    GetWindowRect(hwnd, out r);
    int w = r.Right - r.Left;
    int h = r.Bottom - r.Top;
    if (w < 10 || h < 10) throw new Exception("bad window size " + w + "x" + h);
    using (Bitmap bmp = new Bitmap(w, h, PixelFormat.Format32bppArgb)) {
      using (Graphics g = Graphics.FromImage(bmp)) {
        IntPtr hdc = g.GetHdc();
        bool ok = PrintWindow(hwnd, hdc, PW_RENDERFULLCONTENT);
        g.ReleaseHdc(hdc);
        if (!ok) {
          g.CopyFromScreen(r.Left, r.Top, 0, 0, new Size(w, h), CopyPixelOperation.SourceCopy);
        }
      }
      bmp.Save(path, ImageFormat.Png);
    }
  }
}
'@
$drawing = [Reflection.Assembly]::LoadWithPartialName("System.Drawing").Location
Add-Type -TypeDefinition $cs -ReferencedAssemblies @($drawing) -ErrorAction SilentlyContinue
if (-not ([System.Management.Automation.PSTypeName]'DhuWindowCapture').Type) {
  Add-Type -TypeDefinition $cs -ReferencedAssemblies @($drawing)
}

$p = Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue |
    Where-Object {
        $_.MainWindowHandle -ne [IntPtr]::Zero -and
        ($_.MainWindowTitle -match "Desktop Head Unit|Android Auto")
    } |
    Select-Object -First 1
if (-not $p) {
    $p = Get-Process -Name "desktop-head-unit" -ErrorAction SilentlyContinue |
        Where-Object { $_.MainWindowHandle -ne [IntPtr]::Zero } |
        Select-Object -First 1
}
if (-not $p) {
    Write-Error "capture-dhu-window: desktop-head-unit window not found (is DHU open?)"
}
Write-Host ("capture-dhu-window: pid={0} title={1}" -f $p.Id, $p.MainWindowTitle)
# Retry once — first capture can race a minimize/restore.
[DhuWindowCapture]::Capture($p.MainWindowHandle, $OutFile)
Start-Sleep -Milliseconds 400
[DhuWindowCapture]::Capture($p.MainWindowHandle, $OutFile)
$len = (Get-Item $OutFile).Length
if ($len -lt 30000) {
    Write-Warning "capture-dhu-window: small PNG ($len bytes) - is DHU in front / projecting?"
}
Write-Host ("OK  {0}  ({1} bytes)" -f $OutFile, $len) -ForegroundColor Green
