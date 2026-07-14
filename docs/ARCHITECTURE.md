# Architecture

The project keeps playback decisions independent from any native transport implementation.

```text
Android app -> DashSource -> MPD parser / adaptation / segment downloader
                                      |
                                      v
                               TransportClient SPI
                               /                 \
                    HTTPS compatibility        external provider
                    (included, no Chorus)       (not included)
```

`transport-api` contains no Android or XQUIC types. A segment request may carry a zero-based chunk index, expected delivery time in milliseconds, and the algorithm name. The request is fully built after the bitrate decision and before `TransportClient.execute`/`enqueue`; this ordering lets a compatible provider send client QoE metadata before it sends the HTTP request.

`ChorusAdaptationLogic` stores per-session throughput history. Fresh server path statistics are preferred; otherwise it falls back to the harmonic mean of the latest five completed media-segment samples. The HTTP provider exposes no Chorus capabilities and ignores no metadata secretly—it simply offers ordinary HTTPS DASH compatibility.

The public source tree intentionally stops at the Java SPI. A future redistributable provider should live in a separate module so the DASH/player modules never include proprietary C headers or native constants.
