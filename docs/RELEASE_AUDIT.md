# Local source-release audit

Audit date: 2026-07-14

Audited root commit: `b26a4a2`

Publication state: local candidate only; no GitHub repository or release was created.

## Included boundary

The candidate contains 119 tracked source, resource, build, test, and documentation files. New project files are Copyright 2026 Gerui Lv and Qingyue Tan. Modified MediaPlayer-Extended files preserve the upstream notice and identify the 2026 modifications.

All prior TekiXquic, XQUIC, JNI/C/C++ glue, modified headers, native libraries, APKs, papers, measurements, logs, private configuration, and prior Git history were excluded by whitelist export. The resulting repository has one independent root commit and no relation to the private repository history.

## Automated evidence

| Check | Result |
|---|---|
| Fresh clone release guard | passed; no prohibited artifact or high-risk pattern |
| Fresh clone `./gradlew test assembleStubDebug lintStubDebug` | passed; 198 tasks |
| JVM/Android unit test executions | 45; 0 failures; 0 errors |
| Android lint | no issues found |
| Git staged whitespace check | passed |
| Forbidden tracked extensions | none |
| Unexpected tracked file larger than 1 MiB | none |
| `git fsck --full` after pruning unreachable setup objects | passed |
| Reachable-history prohibited-extension scan | no matches |
| CITATION YAML and SPDX JSON syntax | valid |
| Shell script syntax | valid |

The clean-clone build used Temurin JDK 17, Android SDK Platform 35, Android Build Tools 35.0.0, Android Gradle Plugin 8.7.3, and the checked-in Gradle 8.9 wrapper. Dependency lockfiles and the source-level SPDX SBOM are tracked.

## Deliberately unverified

- No native provider is distributed, so native ABI/link/symbol validation and `xquicDebug` do not apply to this candidate.
- No Chorus-capable private server or endpoint was used; end-to-end multipath behavior was not tested.
- No physical-device playback, WiFi/cellular binding, network-switching, or Activity lifecycle instrumentation run was performed in this environment.
- GitHub Actions, the hosted gitleaks action, GitHub license detection, and repository protection settings cannot run until a remote repository exists.

These are release-scope limitations, not claims of successful full-system reproduction. Public publication remains gated on an explicit maintainer decision after reviewing this candidate.
