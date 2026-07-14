/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public interface TransportClient extends Closeable {
    TransportCapabilities capabilities();
    TransportResponse execute(TransportRequest request) throws IOException;
    Cancellable enqueue(TransportRequest request, TransportCallback callback);

    default Optional<ServerPathStats> latestServerPathStats() {
        return Optional.empty();
    }

    default void setServerPathStatsListener(ServerPathStatsListener listener) {
        // Providers without server-QoE capability intentionally do nothing.
    }

    @Override
    void close();
}
