# F-Droid reproducible publish

ExpeditionGauge release APKs are meant to rebuild bit-for-bit from this tree.

1. Export `SOURCE_DATE_EPOCH=1700000000` (see `examples/android/README.md`).
2. Build the FOSS APK from `examples/android/` with the locked Gradle toolchain.
3. Compare the local APK hash to the GitHub Release artifact before submit.
4. Listing text lives in `metadata/en-US/` and is mirrored under `fastlane/metadata/android/en-US/`.

No Google Play Services, Firebase, or closed telemetry SDKs are on the production path.
