# Generate static F-Droid store assets (icon + feature graphic).
param()

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

$images = Join-Path $Root "examples\android\metadata\en-US\images"
$fastlaneImages = Join-Path $Root "examples\android\fastlane\metadata\android\en-US\images"
New-Item -ItemType Directory -Force -Path $images, (Join-Path $images "phoneScreenshots"), $fastlaneImages, (Join-Path $fastlaneImages "phoneScreenshots") | Out-Null

$syncIcon = Join-Path $Root "scripts\expedition\sync-app-icon.py"
if (-not (Test-Path (Join-Path $Root "docs\assets\app-icon-512.png"))) {
    Write-Error "generate-fdroid-assets: missing docs/assets/app-icon-512.png"
}
python $syncIcon
if ($LASTEXITCODE -ne 0) {
    Write-Error "generate-fdroid-assets: sync-app-icon.py failed (pip install Pillow)"
    exit 1
}

$py = @'
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

def draw_feature(path: Path) -> None:
    root = Path(r"__ROOT__")
    icon_path = root / "docs/assets/app-icon-512.png"
    img = Image.new("RGB", (1024, 500), (0, 0, 0))
    if icon_path.is_file():
        icon = Image.open(icon_path).convert("RGBA")
        icon.thumbnail((220, 220), Image.Resampling.LANCZOS)
        img.paste(icon, (56, 120), icon)
    draw = ImageDraw.Draw(img)
    try:
        title = ImageFont.truetype("arial.ttf", 48)
        body = ImageFont.truetype("arial.ttf", 28)
    except OSError:
        title = ImageFont.load_default()
        body = ImageFont.load_default()
    draw.text((320, 140), "ExpeditionGauge", fill=(255, 204, 0), font=title)
    draw.text((320, 220), "Offline HUD · Drift · Laps · Graphs", fill=(220, 220, 220), font=body)
    img.save(path)

root = Path(r"__ROOT__")
for base in [root / "examples/android/metadata/en-US/images", root / "examples/android/fastlane/metadata/android/en-US/images"]:
    base.mkdir(parents=True, exist_ok=True)
    draw_feature(base / "featureGraphic.png")
print("generated featureGraphic.png")
'@ -replace "__ROOT__", ($Root -replace '\\', '/')

python -c $py
if ($LASTEXITCODE -ne 0) {
    Write-Error "generate-fdroid-assets: Python/Pillow required (pip install Pillow)"
    exit 1
}
Write-Host "F-Droid static assets OK" -ForegroundColor Green
