# Configuration

The demo accepts a complete HTTPS DASH manifest URL at runtime. It must contain a host and a path ending in `.mpd`; URL queries may follow the filename. The field is empty on first launch. A saved runtime value takes precedence over the optional local Gradle property `chorusMpdUrl`; the tracked default is empty.

`config/player.properties.example` documents the empty local key. Supply it transiently as `-PchorusMpdUrl=https://example.invalid/path/manifest.mpd` or in a user-local Gradle configuration. The value is embedded in that local APK, so never use a credential-bearing URL and never distribute the generated APK. No tracked Android resource stores an endpoint.

Cleartext traffic is disabled. The included provider uses the platform TLS stack and does not disable hostname or certificate verification. A complete URL is redacted to its scheme, the placeholder `<redacted>`, and the manifest filename in application logs; directory path, query, fragment, user info, host, and port are omitted.

A Chorus-capable deployment has additional server-side HTTP/3, ALPN, multipath, client-QoE, and server-feedback requirements. Those are provider/server contracts, not options that this public HTTPS fallback can enable.
