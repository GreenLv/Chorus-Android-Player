# Troubleshooting

- **Java/Gradle error:** confirm `java -version` reports JDK 17 and invoke the checked-in wrapper.
- **Android SDK not found:** set `ANDROID_HOME` or add an untracked `local.properties` containing `sdk.dir=...`.
- **MPD rejected:** provide a complete HTTPS URL with a host and `.mpd` path. Cleartext HTTP is intentionally disabled.
- **MPD loads but playback fails:** confirm the manifest and media use codecs/container features supported by the Android device and MediaPlayer-Extended path.
- **No Chorus/multipath behavior:** expected with the included provider. This repository does not ship a Chorus-capable native provider or server.
- **Provider unavailable:** expected from the public JNI stub. It reports no Chorus capabilities and returns a controlled error; see the native-provider contract before integrating an independently authorized backend.
- **Certificate failure:** repair the server certificate chain and host name. Do not disable TLS verification globally.
- **Issue report:** include only a minimal stack trace and redact hosts, ports, paths, queries, tokens, device identifiers, and local paths.
