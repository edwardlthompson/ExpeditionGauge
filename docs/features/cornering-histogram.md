# Feature: cornering-histogram

> Bin session latG into 0.25 G buckets for a cornering histogram.

## Acceptance criteria

- ✅ Uses absolute latG
- ✅ Default bins are 0–2 G in 0.25 steps
- ✅ Values clamp into the last bin
- ✅ i18n: none (stats)

## Smoke scenario

1. Given samples at 0.1 and 0.6 G
2. When the histogram is built
3. Then bucket 0–0.25 has 1 and 0.50–0.75 has 1+

## Container map

| Layer | Path |
|-------|------|
| Logic | `corneringhistogram/CorneringHistogram.kt` |
| Tests | `app/src/test/.../corneringhistogram/` |

## Tests

- Automated: yes — `CorneringHistogramTest`
- Coverage: abs binning

## Fallback validation

- Why tests are not feasible: N/A (automated tests exist)
- Command: `python3 scripts/agent-run.py feature-gate --stack android`
