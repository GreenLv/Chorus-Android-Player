# Chorus Android Player

Chorus Android Player is the public, player-side Android DASH implementation accompanying the paper **“Chorus: Coordinating Mobile Multipath Scheduling and Adaptive Video Streaming”** (ACM MobiCom 2024).

This source release contains the DASH player, player-side prediction and adaptation logic, a transport service-provider interface, and a clean-room Teki/JNI adapter skeleton. It deliberately does **not** contain the Chorus-modified XQUIC core or headers, server integration, private endpoints, experiment data, APKs, AARs, SDK packages, or prebuilt native libraries. The included HTTP provider is a compatibility path for ordinary HTTPS DASH playback; it does not reproduce Chorus multipath behavior.

## Quick start

Prerequisites: JDK 17 and Android SDK Platform 35.

```bash
./gradlew test assembleStubDebug lintStubDebug :transport-teki:assembleDebug
```

Install the generated debug APK on a test device, enter a complete HTTPS URL ending in `.mpd`, and select **Play**. The input is empty by default; `example.invalid` is shown only as a non-routable example.

## Repository layout

- `app`: minimal Android demonstration app and URL validation.
- `player-core`: MediaPlayer-Extended playback core.
- `player-dash`: generic MPD parsing, segment scheduling, adaptation logic, and Chorus player-side predictor.
- `transport-api`: provider-neutral request, capability, algorithm, and server-path DTOs.
- `transport-http`: HTTPS compatibility provider without Chorus capabilities.
- `transport-stub`: deterministic missing-provider behavior for tests and integration work.
- `transport-teki`: Android/JNI integration seam plus an unavailable native stub; no XQUIC code or binary.

See [Architecture](docs/ARCHITECTURE.md), [Building](docs/BUILDING.md), and [Native provider integration](docs/NATIVE_LIBRARY.md).

## Scope and limitations

The source tree can build and exercise the public player path without any proprietary artifact. A full Chorus deployment additionally requires a legally obtained, compatible transport provider and server implementation. Merely supplying an arbitrary XQUIC library is insufficient: the provider must implement the capability contract, client-QoE metadata, server path-statistics callback, and corresponding server behavior.

The tracked `transport-teki` module compiles its own JNI bridge and fail-closed stub, but it is not the Chorus transport core and is not wired into the public APK. The stub reports no Chorus capabilities and returns a controlled provider-unavailable error. A complete deployment must supply an independently authorized backend that satisfies the documented API version and all required capabilities; private libraries and headers must remain outside this repository.

## Paper and related resources

- Paper: [ACM Digital Library, DOI 10.1145/3636534.3649359](https://doi.org/10.1145/3636534.3649359)
- Public research artifacts: [GreenLv/Chorus](https://github.com/GreenLv/Chorus)
- Demo Video: [Chorus_demo_video.mp4](https://greenlv.github.io/files/2024_MobiCom_Chorus_demo_video.mp4)
- Slides: [Chorus_slides.pdf](https://greenlv.github.io/files/2024_MobiCom_Chorus_slides.pdf)
- Tech Report: [Chorus_tech_report.pdf](https://greenlv.github.io/files/2024_MobiCom_Chorus_tech_report.pdf)
- GetMobile Highlights: [ACM Digital Library, DOI 10.1145/3733892.3733900](https://dl.acm.org/doi/10.1145/3733892.3733900)

## Citation

If you use this player in your research, please cite:

```bibtex
@inproceedings{lv2024chorus,
  title={Chorus: Coordinating Mobile Multipath Scheduling and Adaptive Video Streaming},
  author={Lv, Gerui and Wu, Qinghua and Liu, Yanmei and Li, Zhenyu and Tan, Qingyue and Yang, Furong and Chen, Wentao and Ma, Yunfei and Guo, Hongyu and Chen, Ying and Xie, Gaogang},
  booktitle={Proceedings of the 30th Annual International Conference on Mobile Computing and Networking (MobiCom '24)},
  pages={246--262},
  year={2024},
  publisher={ACM},
  address={New York, NY, USA},
  doi={10.1145/3636534.3649359}
}
```

## License and attribution

New project code is Copyright 2026 Gerui Lv and Qingyue Tan and is licensed under Apache-2.0. Upstream source files retain their original copyright notices. See [`NOTICE`](NOTICE) and [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md).

## Privacy when reporting issues

Do not attach native libraries, APKs, full logs, packet captures, real server URLs, tokens, certificates, internal paths, or experiment data. Reduce logs to the smallest redacted excerpt that reproduces the problem.
