# Generate static F-Droid store assets (icon + feature graphic).
param()

$ErrorActionPreference = "Stop"
. "$PSScriptRoot\_expedition-common.ps1"
$Root = Get-ExpeditionRoot

$images = Join-Path $Root "examples\android\metadata\en-US\images"
$fastlaneImages = Join-Path $Root "examples\android\fastlane\metadata\android\en-US\images"
New-Item -ItemType Directory -Force -Path $images, (Join-Path $images "phoneScreenshots"), $fastlaneImages, (Join-Path $fastlaneImages "phoneScreenshots") | Out-Null

$py = @'
from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

def draw_icon(path: Path) -> None:
    img = Image.new("RGB", (512, 512), (18, 22, 28))
    draw = ImageDraw.Draw(img)
    cx, cy = 256, 256
    for radius, color in [(220, (40, 120, 70)), (150, (200, 180, 40)), (80, (200, 60, 60))]:
        draw.ellipse((cx - radius, cy - radius, cx + radius, cy + radius), outline=color, width=6)
    draw.ellipse((cx - 12, cy - 12, cx + 12, cy + 12), fill=(240, 240, 240))
    try:
        font = ImageFont.truetype("arial.ttf", 72)
    except OSError:
        font = ImageFont.load_default()
    draw.text((150, 430), "ExpeditionGauge", fill=(230, 230, 230), font=font)
    img.save(path)

def draw_feature(path: Path) -> None:
    img = Image.new("RGB", (1024, 500), (12, 16, 22))
    draw = ImageDraw.Draw(img)
    draw.rectangle((0, 0, 1024, 500), fill=(12, 16, 22))
    draw.rectangle((40, 80, 480, 420), outline=(60, 140, 90), width=4)
    draw.rectangle((544, 80, 984, 420), outline=(200, 180, 50), width=4)
    try:
        title = ImageFont.truetype("arial.ttf", 48)
        body = ImageFont.truetype("arial.ttf", 28)
    except OSError:
        title = ImageFont.load_default()
        body = ImageFont.load_default()
    draw.text((56, 36), "ExpeditionGauge", fill=(240, 210, 80), font=title)
    draw.text((56, 440), "Offline HUD · Drift · Laps · Graphs", fill=(200, 200, 200), font=body)
    img.save(path)

root = Path(r"__ROOT__")
for base in [root / "examples/android/metadata/en-US/images", root / "examples/android/fastlane/metadata/android/en-US/images"]:
    base.mkdir(parents=True, exist_ok=True)
    draw_icon(base / "icon.png")
    draw_feature(base / "featureGraphic.png")
print("generated icon.png + featureGraphic.png")
'@ -replace "__ROOT__", ($Root -replace '\\', '/')

python -c $py
if ($LASTEXITCODE -ne 0) {
    Write-Error "generate-fdroid-assets: Python/Pillow required (pip install Pillow)"
    exit 1
}
Write-Host "F-Droid static assets OK" -ForegroundColor Green
