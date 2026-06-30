# Developer / Advanced mode

> Sprint 18 — opt-in raw telemetry and filter tuning.

## Overview

Hidden behind a Settings toggle (off by default). Shows live fusion readout and Madgwick β slider for noise tuning.

## Components

- **`SettingsPreferences.developerModeEnabled`**
- **`DeveloperModeScreen`** — pitch/roll/latG/lonG/heading/speed readout + β slider
- **`SensorFusionEngine.setMadgwickBeta()`** — applies tuning at runtime

## Safety

Does not alter default HUD when disabled. No network or logging side effects.
