/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport.teki;

import org.chorusresearch.player.transport.Cancellable;
import org.chorusresearch.player.transport.ServerPathStatsListener;
import org.chorusresearch.player.transport.TransportCallback;
import org.chorusresearch.player.transport.TransportCapabilities;
import org.chorusresearch.player.transport.TransportRequest;
import org.chorusresearch.player.transport.TransportResponse;

import java.io.Closeable;
import java.io.IOException;

/**
 * Integration seam for a legally supplied transport backend.
 *
 * <p>The public source release includes only an unavailable JNI stub. A real implementation must
 * provide the request path and the QoE hooks without placing XQUIC source or binaries in this
 * repository.</p>
 */
public interface TekiBackend extends Closeable {
    TransportCapabilities capabilities();

    TransportResponse execute(TransportRequest request) throws IOException;

    Cancellable enqueue(TransportRequest request, TransportCallback callback);

    void sendClientQoe(int chunkIndex, long expectedDeliveryTimeMs) throws IOException;

    void setServerPathStatsListener(ServerPathStatsListener listener);

    @Override
    void close();
}
