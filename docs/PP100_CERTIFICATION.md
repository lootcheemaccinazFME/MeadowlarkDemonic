# Demonic PP100 Production Certification Gate

PP100 is evidence-gated. Architecture or a stub provider cannot satisfy these lanes.

| Lane | Required evidence |
|---|---|
| Native time/pitch | Concrete native provider; phase/coherence and duration/pitch reference tests; Android runtime pass |
| ML repair | Concrete denoise/de-click provider/model; fixture quality/regression tests; Android runtime pass |
| ML stems | Concrete separation provider/model; named stem outputs; fixture/regression tests; Android runtime pass |
| Standards metering | EBU R128 integrated loudness + true-peak provider; published/reference vectors pass; Android runtime pass |
| Android plugin host | Real discovery/open/process/close lifecycle; unsupported-plugin isolation; Android runtime pass |
| Motorola certification | Physical target-family playback/recording, routing, interruption, long-session, export, latency/underrun evidence |
| Samsung certification | Physical Galaxy playback/recording, routing, interruption, long-session, export, latency/underrun evidence |

ProductionCertification is the canonical PP100 evidence gate. productionReady() must remain false until every lane has recorded provider/version evidence with both reference and runtime tests passing.

Preview Java DSP is useful fallback behavior but does not certify HQ/native/ML/standards lanes.
