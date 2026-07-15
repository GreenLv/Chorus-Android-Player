# Teki/JNI provider contract

`transport-teki` is a public Android/JNI adaptation layer, not the Chorus-modified XQUIC transport. It implements the existing `TransportClient` SPI and compiles against a project-owned native stub. With that stub, API version and capability flags are zero, requests fail with a controlled provider-unavailable error, and Chorus mode cannot be selected.

## Public QoE boundary

The JNI bridge contains only the client/server QoE information flow disclosed by the Chorus technical report:

- Client QoE: zero-based media chunk index and expected delivery time in milliseconds.
- Server QoE: monotonically assigned sequence number, fast/slow path indexes, per-path receive rates in Mbps, per-path RTTs in milliseconds, fast-path traffic ratio in `[0,1]`, and an Android elapsed-realtime receive timestamp in milliseconds.
- Provider capabilities: API version 1 plus HTTP/3, multipath, client-QoE, and server-QoE flags.

The player and provider must preserve this order:

1. complete the ABR decision and expected-time calculation;
2. publish client QoE for the media segment;
3. issue the corresponding transport request;
4. accept asynchronous server QoE updates;
5. return the response or a redacted error.

`TransportCapabilities.requireChorus()` must succeed before Chorus mode is enabled. A provider with a mismatched API version or any missing capability must remain unavailable.

## External backend seam

An independently authorized backend can implement `transport-teki/src/main/cpp/include/chorus_teki_backend.h` and provide its own request path through `TekiBackend`. Integration must happen in a downstream/private build; this repository does not define a library-drop directory and does not auto-discover `.so` files.

Do not contribute XQUIC source or headers, `libxquic.so`, AAR/APK files, SDK/build packages, generated `.cxx` trees, private endpoints, server code, or diagnostic output. The release guard rejects these classes of artifact. A detailed file-by-file provenance inventory remains in the private development repository rather than the public release.
