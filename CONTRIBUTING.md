# Contributing

By contributing, you agree that your contribution is licensed under Apache-2.0 and that you have the right to submit it.

Use JDK 17, keep changes scoped, preserve upstream copyright headers, and add tests for behavior changes. Before opening a pull request, run:

```bash
./scripts/verify-no-private-artifacts.sh
./gradlew test assembleStubDebug lintStubDebug
```

Do not commit native binaries, generated packages, credentials, complete URLs for private infrastructure, logs, packet captures, experiment results, paper PDFs, or local configuration. Do not paste any of those into issues or pull requests. New source files owned by this project should use `Copyright 2026 Gerui Lv and Qingyue Tan` and the Apache-2.0 SPDX identifier or license notice; never replace an upstream author's notice.
