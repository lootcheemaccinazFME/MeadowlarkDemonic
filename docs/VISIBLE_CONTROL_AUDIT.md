# Visible Control Audit

Audit target: canonical Android shell in MainActivity.

## Rule
Every visible control must be one of:
1. Functional and connected to canonical project state.
2. Disabled or intercepted with an explicit reason.
3. Explicitly marked ADAPTER PENDING / DEVICE EVIDENCE PENDING.

## Current shell
- Transport PLAY/STOP/REC: functional.
- HOME / DAW / TV / Agent-AI / AI Video / Comics-Design / Assets / Deliver routes: functional routes.
- Emulator / FME Games / Writing: intercepted with ADAPTER PENDING; they no longer silently route HOME.
- Arrange undo/redo/quantize: functional when applicable.
- Record arm/monitor: functional project controls; microphone/device validation remains hardware-gated.
- Compose/MIDI: sequencer step, swing, logical CC mapping functional; physical USB/Bluetooth validation pending.
- Hyphy: swing, microtiming, roll and 808 slides functional.
- Mix: master gain functional.
- Collaboration: branch/editor metadata functional; remote synchronization explicitly pending.
- Live: modes and multitrack-capture project state functional; network broadcast transport explicitly pending.
- Deliver: credits and remix-parent metadata functional.
- TV: import, authorized/public live channels, category filtering, playback state, surface, favorites, CC/audio preferences functional.
- Image/Video: timeline/transform controls functional when media exists.

This audit does not certify physical audio/MIDI devices, network broadcast, external plugin hosting, or production signing.
