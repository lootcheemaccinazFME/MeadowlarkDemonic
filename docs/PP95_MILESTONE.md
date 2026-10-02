# Demonic PP 95 Milestone Gate

PP 95 means the standalone Android production milestone has its shared architecture, project lifecycle, persistence, routing, recovery, and core operational controllers implemented and build-verified.

## Included in PP 95
- Canonical AudioGraph routing, track/mixer sends and buses.
- Project-scoped Demonic add-on Workspace.
- Rack compiler plus sampler, vocal and stem graph compilation.
- Backward-compatible add-on persistence and crash recovery.
- Extended persistence for racks, automation curves, markers, notes, Live/Looper, Vault metadata, warp markers, repair plans, video sync and Director scenes.
- Version Vault checkpoint/restore controller.
- Automatic project Asset -> Demonic Vault synchronization.
- Rendered PCM peak/RMS/stereo/phase metering with provisional loudness approximation.
- Quantized Live/Looper controller.
- Milestone integration verifier.

## Explicitly outside the PP 95 claim
The following require production DSP/platform adapters or physical-device validation and must not be reported as finished merely because their domain models exist:
- ML stem separation.
- Production denoise/de-click/de-hum.
- High-quality phase-coherent time-stretch and pitch-shift.
- Standards-compliant integrated LUFS/true-peak measurement.
- External Android plugin hosting.
- Full Motorola/Samsung physical-device audio latency and stability matrix.

These items form the remaining hardening/PP 95->100 lane.
