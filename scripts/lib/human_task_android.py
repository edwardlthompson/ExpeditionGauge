"""ADB / Android HUMAN automation handlers."""
from __future__ import annotations

import os
import shutil
import subprocess
from pathlib import Path

from human_task_core import AttemptResult, run_cmd


def adb_authorized(root: Path) -> bool:
    adb = os.environ.get("ADB", "adb")
    if os.name == "nt" and not shutil.which(adb):
        win = os.environ.get("LOCALAPPDATA", "")
        if win:
            candidate = Path(win) / "Android/Sdk/platform-tools/adb.exe"
            if candidate.is_file():
                adb = str(candidate)
    try:
        out = subprocess.run(
            [adb, "devices"],
            capture_output=True,
            text=True,
            check=False,
        )
    except FileNotFoundError:
        return False
    if out.returncode != 0:
        return False
    for line in out.stdout.splitlines()[1:]:
        if line.strip().endswith("device"):
            return True
    return False

def automate_adb_instrumented(root: Path, _cfg: dict) -> AttemptResult:
    if adb_authorized(root):
        verify = root / "scripts/verify-android-insets.sh"
        if verify.is_file():
            code, tail = run_cmd(root, ["bash", str(verify)])
            if code == 0:
                return AttemptResult(0, "verify-android-insets", "ADB instrumented tests passed", False)
            return AttemptResult(1, "verify-android-insets", tail or f"exit {code}", True)
        gradle = root / "examples/android/gradlew"
        if gradle.is_file():
            code, tail = run_cmd(
                root,
                ["bash", str(gradle), "connectedDebugAndroidTest"],
                cwd=root / "examples/android",
            )
            if code == 0:
                return AttemptResult(0, "connectedDebugAndroidTest", "connectedDebugAndroidTest passed", False)
            return AttemptResult(1, "connectedDebugAndroidTest", tail or f"exit {code}", True)
    gradle = root / "examples/android/gradlew"
    if gradle.is_file():
        run_cmd(root, ["bash", str(gradle), "test"], cwd=root / "examples/android")
    return AttemptResult(
        1,
        "adb-unavailable",
        "no_authorized_device; unit tests run if Android tree present",
        True,
    )


def automate_fdroid_dry_run(root: Path, _cfg: dict) -> AttemptResult:
    script = root / "scripts/fdroid-device-dry-run.sh"
    if not script.is_file():
        return AttemptResult(1, "fdroid-dry-run", "fdroid-device-dry-run.sh missing", True)
    if not adb_authorized(root):
        return AttemptResult(1, "fdroid-dry-run", "no_authorized_device", True)
    code, tail = run_cmd(root, ["bash", str(script)])
    if code == 0:
        return AttemptResult(0, "fdroid-dry-run", "F-Droid device dry-run passed", False)
    return AttemptResult(1, "fdroid-dry-run", tail or f"exit {code}", True)


def automate_android_sdk_smoke(root: Path, _cfg: dict) -> AttemptResult:
    gradle = root / "examples/android/gradlew"
    if gradle.is_file():
        code, tail = run_cmd(root, ["bash", str(gradle), "test"], cwd=root / "examples/android")
        if code != 0:
            return AttemptResult(1, "gradle-test", tail or f"exit {code}", True)
    if adb_authorized(root):
        adb = os.environ.get("ADB", "adb")
        code, _ = run_cmd(root, [adb, "shell", "getprop", "ro.build.version.sdk"])
        if code == 0:
            return AttemptResult(0, "adb-getprop", "Gradle tests + adb getprop smoke", False)
    if gradle.is_file():
        return AttemptResult(1, "adb-unavailable", "no_authorized_device after unit tests", True)
    return AttemptResult(1, "android-sdk", "No Android example tree", True)


def _pwsh() -> str:
    return shutil.which("pwsh") or shutil.which("powershell") or "pwsh"


def automate_pwsh_adb(root: Path, rel: str, args: list[str], method: str) -> AttemptResult:
    script = root / rel
    if not script.is_file():
        return AttemptResult(1, method, f"{rel} missing", True)
    if not adb_authorized(root):
        return AttemptResult(1, method, "no_authorized_device", True)
    code, tail = run_cmd(root, [_pwsh(), "-NoProfile", "-File", str(script), *args])
    if code == 0:
        return AttemptResult(0, method, tail or "ok", False)
    return AttemptResult(1, method, tail or f"exit {code}", True)


def automate_crash_review_smoke(root: Path, _cfg: dict) -> AttemptResult:
    return automate_pwsh_adb(
        root,
        "scripts/expedition/adb-smoke.ps1",
        ["-Sprint", "32", "-Scenario", "crash-review-smoke"],
        "crash-review-smoke",
    )


def automate_emulator_hud_smoke(root: Path, _cfg: dict) -> AttemptResult:
    return automate_pwsh_adb(
        root,
        "scripts/expedition/adb-smoke.ps1",
        ["-Sprint", "32", "-Scenario", "emulator-hud-smoke"],
        "emulator-hud-smoke",
    )


def automate_inclinometer_landscape(root: Path, _cfg: dict) -> AttemptResult:
    return automate_pwsh_adb(
        root,
        "scripts/expedition/adb-smoke.ps1",
        ["-Sprint", "32", "-Scenario", "inclinometer-landscape-pack"],
        "inclinometer-landscape-pack",
    )


def automate_dhu_screenshot_ci(root: Path, _cfg: dict) -> AttemptResult:
    dhu = (
        Path(os.environ.get("LOCALAPPDATA", ""))
        / "Android/Sdk/extras/google/auto/desktop-head-unit.exe"
    )
    if os.name == "nt" and not dhu.is_file():
        return AttemptResult(1, "dhu-screenshot-ci", "DHU binary missing", True)
    return automate_pwsh_adb(
        root,
        "scripts/expedition/dhu-smoke.ps1",
        ["-RestartDhu"],
        "dhu-screenshot-ci",
    )
