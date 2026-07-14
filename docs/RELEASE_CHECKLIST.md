# Source release checklist

- [ ] Unit tests, `assembleStubDebug`, and `lintStubDebug` pass from a clean clone.
- [ ] `scripts/verify-no-private-artifacts.sh` passes in the worktree and Git index.
- [ ] `git rev-list --objects --all` contains no binary, PDF, log, result, or private configuration.
- [ ] No unexpected tracked file exceeds 1 MiB.
- [ ] LICENSE, NOTICE, third-party notices, provenance, and citation metadata were reviewed.
- [ ] No native library, APK/AAB/AAR, symbol archive, paper PDF, or measurement is attached to the release.
- [ ] Documentation does not claim that this source release contains the Chorus transport core or server.
- [ ] A maintainer explicitly approves publication after reviewing all scanner dispositions.
