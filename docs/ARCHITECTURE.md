# Architecture

The project keeps playback decisions independent from any native transport implementation.

```text
Android app -> DashSource -> MPD parser / adaptation / segment downloader
                                      |
                                      v
                               TransportClient SPI
                         /             |              \
              HTTPS compatibility   missing stub    Teki/JNI adapter
              (included, no Chorus)  (included)      (stub included;
                                                      backend external)
```

`transport-api` contains no Android or XQUIC types. A segment request may carry a zero-based chunk index, expected delivery time in milliseconds, and the algorithm name. The request is fully built after the bitrate decision and before `TransportClient.execute`/`enqueue`; this ordering lets a compatible provider send client QoE metadata before it sends the HTTP request.

`ChorusAdaptationLogic` stores per-session throughput history. Fresh server path statistics are preferred; otherwise it falls back to the harmonic mean of the latest five completed media-segment samples. The HTTP provider exposes no Chorus capabilities and ignores no metadata secretly—it simply offers ordinary HTTPS DASH compatibility.

`transport-teki` implements the existing Java SPI without changing it. Its newly written JNI layer exposes only the client-QoE and server-QoE boundary described in the Chorus technical report. The built-in C backend reports API version 0 and no capabilities, so it cannot accidentally enter Chorus mode and fails requests with a controlled provider-unavailable error.

An authorized backend may implement the small C contract in `chorus_teki_backend.h` outside this source release. The DASH/player modules remain independent of Android networking details, XQUIC headers, native constants, and backend ownership.
