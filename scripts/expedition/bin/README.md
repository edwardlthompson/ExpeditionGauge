# `run-as-uid` Android Auto helper

Tiny `setuid`/`setgid` + `exec` helper used when Magisk `su` is missing and `adb root` is available. **Not part of the APK.** F-Droid does not ship this binary.

| File | Role |
|------|------|
| `run-as-uid.c` | Source (MIT, same as this repo) |
| `run-as-uid-arm64` | NDK aarch64 PIE, **gitignored** — build on demand |

```powershell
pwsh scripts/expedition/build-run-as-uid.ps1
```

```bash
bash scripts/expedition/build-run-as-uid.sh
```

Needs an Android NDK (`ANDROID_NDK_HOME` or `$ANDROID_SDK_ROOT/ndk/<rev>`). `pack-aa-install-kit.ps1` compiles before zipping so GitHub Release kits still include the helper.
