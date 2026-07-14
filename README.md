# Chorus Android Player

Chorus Android Player is the public, player-side Android DASH implementation accompanying the paper **“Chorus: Coordinating Mobile Multipath Scheduling and Adaptive Video Streaming”** (ACM MobiCom 2024).

This source release contains the DASH player, player-side prediction and adaptation logic, and a transport service-provider interface. It deliberately does **not** contain the Chorus-modified XQUIC core, server integration, JNI glue, private endpoints, experiment data, APKs, or prebuilt native libraries. The included HTTP provider is a compatibility path for ordinary HTTPS DASH playback; it does not reproduce Chorus multipath behavior.

## Quick start

Prerequisites: JDK 17 and Android SDK Platform 35.

```bash
./gradlew test assembleStubDebug lintStubDebug
```

Install the generated debug APK on a test device, enter a complete HTTPS URL ending in `.mpd`, and select **Play**. The input is empty by default; `example.invalid` is shown only as a non-routable example.

## Repository layout

- `app`: minimal Android demonstration app and URL validation.
- `player-core`: MediaPlayer-Extended playback core.
- `player-dash`: generic MPD parsing, segment scheduling, adaptation logic, and Chorus player-side predictor.
- `transport-api`: provider-neutral request, capability, algorithm, and server-path DTOs.
- `transport-http`: HTTPS compatibility provider without Chorus capabilities.
- `transport-stub`: deterministic missing-provider behavior for tests and integration work.

See [Architecture](docs/ARCHITECTURE.md), [Building](docs/BUILDING.md), and [Native provider integration](docs/NATIVE_LIBRARY.md). The public API names map to stable legacy native values as documented in [Algorithm mapping](docs/ALGORITHM_MAPPING.md).

## Scope and limitations

The source tree can build and exercise the public player path without any proprietary artifact. A full Chorus deployment additionally requires a legally obtained, compatible transport provider and server implementation. Merely supplying an arbitrary XQUIC library is insufficient: the provider must implement the capability contract, client-QoE metadata, server path-statistics callback, and corresponding server behavior.

This release has no tracked native provider and therefore has no `xquicDebug` build variant. The public contract is intentionally Java-only until a redistributable provider can be audited. Never place a private library inside a commit; use the ignored `local-native-libs/` layout described in the integration guide.

## Paper and data

- Paper: [ACM Digital Library, DOI 10.1145/3636534.3649359](https://doi.org/10.1145/3636534.3649359)
- Public research artifacts: [GreenLv/Chorus](https://github.com/GreenLv/Chorus)
- Citation metadata: [`CITATION.cff`](CITATION.cff)

The paper PDF and technical report are linked, not redistributed, by this repository.

## License and attribution

New project code is Copyright 2026 Gerui Lv and Qingyue Tan and is licensed under Apache-2.0. Upstream source files retain their original copyright notices. See [`NOTICE`](NOTICE), [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md), [`docs/PROVENANCE.md`](docs/PROVENANCE.md), and the source-level [`SPDX SBOM`](docs/SBOM.spdx.json).

## Privacy when reporting issues

Do not attach native libraries, APKs, full logs, packet captures, real server URLs, tokens, certificates, internal paths, or experiment data. Reduce logs to the smallest redacted excerpt that reproduces the problem.
