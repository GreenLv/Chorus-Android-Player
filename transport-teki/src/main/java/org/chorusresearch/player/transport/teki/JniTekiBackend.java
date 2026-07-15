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

import java.io.IOException;
import java.util.Objects;

/** JNI-backed fail-closed provider included with the source release. */
final class JniTekiBackend implements TekiBackend {
    private final NativeTekiBridge bridge = new NativeTekiBridge();

    @Override
    public TransportCapabilities capabilities() {
        int apiVersion = bridge.backendApiVersion();
        int flags = bridge.capabilityFlags();
        if (apiVersion < TransportCapabilities.UNAVAILABLE_API_VERSION) {
            apiVersion = TransportCapabilities.UNAVAILABLE_API_VERSION;
            flags = 0;
        }
        return new TransportCapabilities(
                apiVersion,
                (flags & NativeTekiBridge.CAPABILITY_HTTP3) != 0,
                (flags & NativeTekiBridge.CAPABILITY_MULTIPATH) != 0,
                (flags & NativeTekiBridge.CAPABILITY_CLIENT_QOE) != 0,
                (flags & NativeTekiBridge.CAPABILITY_SERVER_QOE) != 0);
    }

    @Override
    public TransportResponse execute(TransportRequest request) throws IOException {
        Objects.requireNonNull(request, "request");
        throw unavailable();
    }

    @Override
    public Cancellable enqueue(TransportRequest request, TransportCallback callback) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(callback, "callback");
        callback.onFailure(unavailable());
        return FailedCancellable.INSTANCE;
    }

    @Override
    public void sendClientQoe(int chunkIndex, long expectedDeliveryTimeMs) throws IOException {
        if (!bridge.sendClientQoe(chunkIndex, expectedDeliveryTimeMs)) throw unavailable();
    }

    @Override
    public void setServerPathStatsListener(ServerPathStatsListener listener) {
        bridge.setServerPathStatsListener(listener);
    }

    @Override
    public void close() {
        bridge.close();
    }

    private IOException unavailable() {
        return new IOException("authorized Teki/XQUIC backend is not included in this source release");
    }

    private enum FailedCancellable implements Cancellable {
        INSTANCE;

        @Override
        public void cancel() {}

        @Override
        public boolean isCancelled() {
            return true;
        }
    }
}
