# Parameters and deviations

The same paper-emulation preset is available in machine-readable form at `config/paper-preset.properties`. It is a reference preset, not an automatic override for arbitrary MPDs.

| Parameter | Paper/technical report | Public player behavior | Scope and source |
|---|---|---|---|
| Media chunk duration | 4 s experimental preset | read from each MPD representation | 4 s is a paper preset, not a restriction on generic playback |
| Bitrate ladder | 1, 2.5, 5, 8, 16 Mbps | read and sorted from the MPD | the paper ladder is not hardcoded |
| MPC horizon | 5 chunks | `MpcDecisionEngine.DEFAULT_HORIZON` = 5 | player-side/tested; exhaustive choices are `5^5 = 3125` for five representations |
| Throughput history | latest 5 samples | `ChorusAdaptationLogic.HM_SAMPLE_COUNT` = 5 | HM fallback from the technical report |
| Server feedback | approximately every 200 ms | receiver accepts provider updates; freshness default is 1000 ms | 200 ms is server behavior, not a fabricated client timer |
| Target buffer | 30 s in virtual-player emulation | MPD/library minimum-buffer behavior | the public Android player does not copy the virtual-player value |
| Rebuffer penalty | 16 in paper preset | caller-provided MPC input | no fixed generic-player claim |
| Smoothness penalty | 1 in paper preset | caller-provided MPC input | no fixed generic-player claim |
| Session duration | 5 min emulation | no forced stop | experiment duration is not an application default |

## Expected delivery time

Before a media request, the public adaptation logic uses a supplied `SegmentSizeProvider` when available. Otherwise it estimates bytes from MPD representation bandwidth and segment duration. Expected time is that size divided by the most recent throughput prediction. The estimate fallback is an implementation choice, not a claim that a standard MPD exposes exact future segment sizes.

## Deviation ledger

- The full paper MPC/control loop depends on the excluded transport core and server. `MpcDecisionEngine` is a deterministic, tested player-side primitive, not a claim of full-system reproduction.
- The Android public path uses the MPD's ladder and duration instead of the emulation preset.
- The HTTPS provider enables generic playback but cannot create server-QoE feedback or multipath semantics.
