# Provenance

This repository was assembled by whitelist export into a new history. No file was copied from the private repository merely because it was present there.

| Area | Provenance | Public-release basis |
|---|---|---|
| `player-core` | MediaPlayer-Extended `v4.4.1` / `a1ac9476ec59f690b63e32cb52917495e024f748` | upstream Apache-2.0 source with original notices |
| base `player-dash` parser/player classes | same MediaPlayer-Extended baseline | upstream Apache-2.0; modified files retain upstream notice and identify 2026 modifications |
| `transport-api`, `transport-http`, `transport-stub` | clean-room Java contract and providers written for this release | Copyright 2026 Gerui Lv and Qingyue Tan, Apache-2.0 |
| `ChorusAdaptationLogic`, `MpcDecisionEngine`, app and release documentation | new player-side implementation from public paper semantics and generic engineering requirements | Copyright 2026 Gerui Lv and Qingyue Tan, Apache-2.0 |

Explicitly excluded: TekiXquic source, all existing project JNI/C/C++ glue, modified XQUIC headers, cJSON/libev snapshots, native build files, `libxquic.so`, APKs, PDFs, logs, measurements, private configuration, and the prior Git history.

The research prototype used MediaPlayer-Extended and TekiXquic, and TekiXquic's Maven 1.0.8 POM declares Apache-2.0. Because the private snapshot and later modifications were not sufficiently attributable file by file, this release does not redistribute any of that code. This conservative exclusion is independent of the upstream POM statement.
