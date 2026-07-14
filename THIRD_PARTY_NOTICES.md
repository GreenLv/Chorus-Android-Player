# Third-party notices

The root Apache-2.0 license applies to the original Chorus Android Player code. Third-party components retain their own notices.

| Component | Version/baseline | Use | License evidence |
|---|---|---|---|
| MediaPlayer-Extended | tag `v4.4.1`, commit `a1ac9476ec59f690b63e32cb52917495e024f748` | `player-core` and the original basis of `player-dash` | Upstream `LICENSE`, Apache-2.0; original source headers retained |
| Okio | `1.17.6` | byte sink in `player-dash` | `player-dash/LICENSE_OKIO`, Apache-2.0 |
| ISO Parser | `1.1.22` | fragmented MP4 processing | `player-dash/LICENSE_ISOPARSER`, Apache-2.0 |
| JUnit | `4.13.2` | test scope only | Eclipse Public License 1.0 |

TekiXquic and XQUIC are architectural antecedents of the research prototype, but their source and binaries are not distributed here. Their absence is intentional and should not be interpreted as a license grant or a representation that this repository implements the private transport core.
