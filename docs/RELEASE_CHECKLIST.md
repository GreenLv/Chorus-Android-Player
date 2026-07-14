# Source release checklist

- [x] Unit tests, `assembleStubDebug`, and `lintStubDebug` pass from a clean clone.
- [x] `scripts/verify-no-private-artifacts.sh` passes in the worktree and Git index.
- [x] `git rev-list --objects --all` contains no binary, PDF, log, result, or private configuration.
- [x] No unexpected tracked file exceeds 1 MiB.
- [x] LICENSE, NOTICE, third-party notices, provenance, and citation metadata were reviewed.
- [x] No native library, APK/AAB/AAR, symbol archive, paper PDF, or measurement is attached to the local candidate.
- [x] Documentation does not claim that this source release contains the Chorus transport core or server.
- [ ] A maintainer explicitly approves publication after reviewing all scanner dispositions.
