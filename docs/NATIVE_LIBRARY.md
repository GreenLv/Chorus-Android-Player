# External native provider

No native provider, XQUIC header, JNI glue, or prebuilt library is included or downloaded by this project. The following ignored layout is reserved for users who independently possess a legally distributable compatible implementation:

```text
local-native-libs/
├── include/
└── jniLibs/
    ├── arm64-v8a/libxquic.so
    └── armeabi-v7a/libxquic.so
```

Placing a file there does not integrate it automatically. An adapter module must implement `TransportClient`, declare API version 1, and truthfully report HTTP/3, multipath, client-QoE, and server-QoE capabilities. Before starting Chorus mode, call `TransportCapabilities.requireChorus()` and fail fast if any capability is absent.

Stable public algorithm IDs are 0 through 4 as listed in `ALGORITHM_MAPPING.md`. Media-segment chunk indexes are zero-based and expected delivery time uses milliseconds. Server statistics use Mbps, milliseconds, a fast-path ratio in `[0,1]`, and an elapsed-realtime receive timestamp so freshness does not depend on wall-clock changes.

An adapter must enforce this event order:

1. complete the ABR decision and expected-time calculation;
2. send client QoE metadata for the media segment;
3. send the HTTP request;
4. accept asynchronous server path-statistics feedback;
5. return the response or a redacted error.

Use `scripts/verify-native-provider.sh /path/to/provider/root ABI` for basic local layout checks. It never downloads or copies a library. Do not submit a provider or its diagnostic output to this repository unless you are authorized to redistribute it.
