# PP100 Release Evidence Gate

PP100 is granted only when every item below has attached evidence.

## Automated
- [ ] Unit tests pass, including schema-v4 matrix round trip.
- [ ] Android lint passes.
- [ ] Debug APK assembles.
- [ ] Release APK assembles with configured FME signing credentials.
- [ ] Recovery test proves active snapshot fallback.
- [ ] Visible-control audit finds no silent HOME fallbacks or unlabeled placeholders.
- [ ] Audio runtime smoke test completes without underrun/error in supported test environment.

## Motorola physical device
- [ ] Install signed release APK.
- [ ] Cold launch / warm launch / rotate-resume.
- [ ] Touch navigation and every enabled control.
- [ ] Playback, record permission, record/monitor, stop/restart.
- [ ] USB/Bluetooth MIDI detection, mapping, calibration and reconnect.
- [ ] 15-minute playback/record performance run with crash/ANR notes.
- [ ] Force-stop/relaunch recovery restores project state.

## Samsung Galaxy physical device
- [ ] Install signed release APK.
- [ ] Cold launch / warm launch / rotate-resume.
- [ ] Touch navigation and every enabled control.
- [ ] Playback, record permission, record/monitor, stop/restart.
- [ ] USB/Bluetooth MIDI detection, mapping, calibration and reconnect.
- [ ] 15-minute playback/record performance run with crash/ANR notes.
- [ ] Force-stop/relaunch recovery restores project state.

## Truth gate
Hardware, external plugin hosting, broadcast networking, and device-audio validation remain PENDING until their rows have real evidence. A successful CI build alone never marks them READY.
