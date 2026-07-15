# Building

## Required tools

- JDK 17
- Android SDK Platform 35
- Android SDK Build Tools 35.0.0
- Android SDK NDK and CMake supported by Android Gradle Plugin 8.7.3

The repository pins Android Gradle Plugin 8.7.3 and Gradle 8.9. Android Studio is optional.
The minimum application API is 24 (Android 7.0), and the target/compile API is 35.

Set `ANDROID_HOME` or create an untracked `local.properties` with `sdk.dir=/absolute/path/to/sdk`, then run:

```bash
./gradlew test assembleStubDebug lintStubDebug :transport-teki:assembleDebug
```

`app/build/outputs/apk/stub/debug/` contains the locally generated debug APK. It is ignored and must not be attached to a source release.

The `stub` product flavor names the public, no-private-dependency APK build. It currently wires the HTTPS compatibility provider so generic MPDs can be exercised; it does not expose HTTP/3, multipath, client-QoE, or server-QoE capabilities. The separate `transport-teki` task compiles the public JNI bridge against its own unavailable backend stub. There is no XQUIC flavor and no XQUIC dependency.

Public CI uses the same wrapper, JDK, and tasks. The build uses only Google Maven, Maven Central, and the Gradle Plugin Portal; it does not use JCenter, Fabric, Bintray, `mavenLocal`, or a private repository.
