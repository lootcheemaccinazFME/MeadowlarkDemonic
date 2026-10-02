# Production Readiness

## Automated gates
- Unit tests and deterministic matrix-state tests must pass.
- Android lint must pass.
- Debug APK must assemble and be non-empty.
- Unsigned release APK must assemble and be non-empty.
- Deterministic audio performance hardening test must pass.
- Canonical project persists on lifecycle pause/stop and through crash-safe ProjectRecovery.
- Visible controls are functional or explicitly capability-gated.
- Android cleartext traffic is disabled and app backup is disabled.

## Release signing
Release signing is configured only through external environment credentials. No private key or password belongs in source control. A CI run without those credentials produces an unsigned release candidate and reports signing evidence PENDING. Production distribution requires a signed release artifact and signature verification evidence.

## Device qualification
Production readiness of the codebase does not imply device certification. Motorola and Samsung qualification requires the physical-device rows in PP100_RELEASE_EVIDENCE_GATE.md. Until then DEVICE_AUDIO_VALIDATION, USB/Bluetooth controller validation, latency measurements and OEM lifecycle behavior remain evidence-gated.

## External adapters
Remote collaboration transport, network live broadcast and external Android plugin hosting remain unavailable/pending unless a concrete provider adapter registers READY and passes runtime/reference tests.
