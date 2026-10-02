# Demonic Android Master Build Matrix

This matrix is the authoritative implementation map for the unified Demonic Android APK. No subsystem may create a competing project, transport, timeline, asset, mixer, or undo authority.

## Canonical spine
DemonicProject -> Transport -> Timeline/Clips -> AudioGraph -> Assets -> Persistence/History.

## Build gates
1. Foundation & Architecture
Project model, navigation, persistence, recovery, provenance.

2. Core Audio Engine
Playback, recording, routing, buffering, latency, resampling.

3. DAW Workspace
Timeline, clips, regions, tracks, transport, looping, editing.

4. Sequencer & Tracker
Step sequencer, patterns, tracker workflow, timing engine.

5. MIDI & Controllers
MIDI I/O, USB/Bluetooth controllers, pads, mapping, calibration.

6. Drum / 808 / Hyphy Engine
Swing, microtiming, probability, rolls, bass slides, Bay-pocket tools.

7. Instrument & Sample Lab
Sampling, slicing, pitch/stretch, instruments, presets, resampling.

8. Vocal & Recording Lab
Punch-ins, takes, comping, stacks, harmonies, ad-libs, monitoring.

9. Mixer / DSP / FX / Mastering
Mixer graph, EQ, dynamics, sends, automation, mastering.

10. Arrangement & Performance
Scenes, section triggering, Live Mutation, performance capture.

11. AI Maestro & Composition
Controlled generation, Freeze DNA, variations, theory/composition engine. AI operations are non-destructive and must produce inspectable project changes.

12. Assets / Projects / Automation
Sound Vault, Smart Browser, Pocket Transfer, macros, branches, snapshots.

13. Collaboration & Remote Studio
Collaborators, permissions, remote booth, synchronized takes, session branches.

14. Live Ecosystem
Livestream, Open Mic, Stage, Jam, Beat Battles, guests, backstage, scenes, multitrack broadcast capture.

15. Community / Publishing / Delivery
Profiles, releases, credits, remix lineage, clips, Replay Studio, sharing/export.

16. APK Hardening & Release
Android performance, Motorola/Samsung testing, crash recovery, permissions, security, offline behavior, full button audit, regression testing, signed release APK.

## Completion law
A gate is COMPLETE only when its implementation exists, is reachable from the canonical shell, preserves the unified spine, has persistence/recovery where applicable, and passes automated tests. Hardware, networking, streaming, latency, codec, MIDI, and device-specific claims additionally require real adapter/device evidence.

## UI law
/mnt/data/23084.png is the canonical visual source of truth for the Android shell. Approved supporting dashboard references may improve panel density and organization but may not replace the FME identity or canonical shell hierarchy.

## Integration law
Demonic TV and channel state remain part of the same persistent project/application architecture. Existing working TV/channel behavior must not be regressed by DAW expansion.

## Release law
A successful debug build is not PP100. PP100 requires the release gate, full button audit, regression pass, crash/recovery evidence, required physical Motorola/Samsung validation, and a signed release APK.
