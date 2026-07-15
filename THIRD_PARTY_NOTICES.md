# Third-party notices

The root Apache-2.0 license applies to the original Chorus Android Player code. Third-party components retain their own notices.

| Component | Version/baseline | Use | License evidence |
|---|---|---|---|
| MediaPlayer-Extended | tag `v4.4.1`, commit `a1ac9476ec59f690b63e32cb52917495e024f748` | `player-core` and the original basis of `player-dash` | Upstream `LICENSE`, Apache-2.0; original source headers retained |
| Okio | `1.17.6` | byte sink in `player-dash` | `player-dash/LICENSE_OKIO`, Apache-2.0 |
| ISO Parser | `1.1.22` | fragmented MP4 processing | `player-dash/LICENSE_ISOPARSER`, Apache-2.0 |
| JUnit | `4.13.2` | test scope only | Eclipse Public License 1.0 |
| TekiXquic | Maven `1.0.8` | API/architecture antecedent for the newly written `transport-teki` adapter | [Published Maven POM](https://repo.maven.apache.org/maven2/io/github/yangqingyuan/teki-quic/1.0.8/teki-quic-1.0.8.pom) declares Apache-2.0; no upstream source or binary is vendored |

The Java and JNI files in `transport-teki` are project code written for this release; they are not copies of the original project-specific JNI implementation. Modified XQUIC remains an architectural antecedent of the research prototype, but its source, headers, binaries, SDK packages, and server components are not distributed here. Their absence is intentional and should not be interpreted as a license grant or a representation that this repository implements the private transport core.
