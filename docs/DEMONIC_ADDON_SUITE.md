# Demonic Add-on Suite

Status: integrated domain layer on `build/unified-demonic-spine`.

## Shared law
All add-ons are project-scoped and must route audio through the existing shared AudioGraph. No add-on may introduce a competing audio engine.

Canonical signal path:
`Input -> Instrument/Sampler -> FX -> Sends -> Bus -> Master -> Export`

## Included modules
Sampler / Drum Machine; Vocal Studio; Mastering Rack; Stem Lab; Time/Pitch Engine; Audio Repair Lab; Automation Curves; Groove Engine; Chord/Scale Assistant; Performance Mode; Looper; Plugin Rack; Asset Vault; Version Vault; Collaboration Package; Lyrics/Notes Workspace; Marker System; Spectrum/Meter Suite; Video Audio-Sync Lab; Director Scene Builder.

## Demonic-native systems
- **Demonic Rack:** universal ordered modular chain with bypass, reorder and parameter state.
- **Demonic Vault:** searchable project-aware asset registry for imported, downloaded, generated and recorded material.
- **Demonic Live:** Performance Mode + Looper + MIDI/clip/scene adapters + TV/Director cue surfaces.

## Android contract
Motorola and Samsung Galaxy are the primary mobile targets. Internal modules implement the PluginModule contract now. External plugin hosting remains capability-gated and must not be reported as available unless the device/platform adapter proves it.

## Integration sequence
1. Bind Workspace lifecycle to DemonicProject/ProjectStore.
2. Compile Rack, sampler, vocal, stem and mastering state into AudioGraph nodes.
3. Bind transport/timeline markers, automation and warp state.
4. Bind recorder/import/downloader/AI ingestion outputs into DemonicVault.
5. Bind Performance Mode/Looper to transport quantization and MIDI.
6. Bind meter taps after buses/master.
7. Bind video sync and Director cues to existing media/TV surfaces.
8. Extend project serialization, recovery and Version Vault checkpoints.
9. Add portable collaboration export.
10. Run compile, emulator launch, audio smoke tests and Motorola/Samsung device matrix.

The domain layer is intentionally platform-neutral Java so it can compile in the Android app without adding a second runtime or FMEUI wrapper.
