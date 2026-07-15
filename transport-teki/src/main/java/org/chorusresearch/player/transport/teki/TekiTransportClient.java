/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport.teki;

import org.chorusresearch.player.transport.Cancellable;
import org.chorusresearch.player.transport.ServerPathStats;
import org.chorusresearch.player.transport.ServerPathStatsListener;
import org.chorusresearch.player.transport.TransportCallback;
import org.chorusresearch.player.transport.TransportCapabilities;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.TransportRequest;
import org.chorusresearch.player.transport.TransportResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TransportClient adapter for the Teki/JNI integration boundary used by the research prototype.
 *
 * <p>Client QoE is sent before the corresponding media request. This release deliberately ships
 * an unavailable JNI backend, so the default constructor fails closed until an authorized backend
 * is supplied.</p>
 */
public final class TekiTransportClient implements TransportClient {
    private final TekiBackend backend;
    private final AtomicBoolean closed = new AtomicBoolean();
    private volatile ServerPathStats latestServerPathStats;

    public TekiTransportClient() {
        this(new JniTekiBackend());
    }

    public TekiTransportClient(TekiBackend backend) {
        this.backend = Objects.requireNonNull(backend, "backend");
        backend.setServerPathStatsListener(stats -> latestServerPathStats = stats);
    }

    @Override
    public TransportCapabilities capabilities() {
        return backend.capabilities();
    }

    @Override
    public TransportResponse execute(TransportRequest request) throws IOException {
        ensureOpen();
        Objects.requireNonNull(request, "request");
        sendClientQoeIfPresent(request);
        return backend.execute(request);
    }

    @Override
    public Cancellable enqueue(TransportRequest request, TransportCallback callback) {
        Objects.requireNonNull(request, "request");
        Objects.requireNonNull(callback, "callback");
        try {
            ensureOpen();
            sendClientQoeIfPresent(request);
            return backend.enqueue(request, callback);
        } catch (IOException exception) {
            callback.onFailure(exception);
            return RejectedCancellable.INSTANCE;
        }
    }

    @Override
    public Optional<ServerPathStats> latestServerPathStats() {
        return Optional.ofNullable(latestServerPathStats);
    }

    @Override
    public void setServerPathStatsListener(ServerPathStatsListener listener) {
        backend.setServerPathStatsListener(stats -> {
            latestServerPathStats = stats;
            if (listener != null) listener.onServerPathStats(stats);
        });
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) backend.close();
    }

    private void sendClientQoeIfPresent(TransportRequest request) throws IOException {
        if (request.chunkIndex() != TransportRequest.NO_CHUNK
                && request.expectedDeliveryTimeMs()
                != TransportRequest.UNKNOWN_EXPECTED_TIME_MS) {
            backend.sendClientQoe(request.chunkIndex(), request.expectedDeliveryTimeMs());
        }
    }

    private void ensureOpen() throws IOException {
        if (closed.get()) throw new IOException("Teki transport client is closed");
    }

    private enum RejectedCancellable implements Cancellable {
        INSTANCE;

        @Override
        public void cancel() {}

        @Override
        public boolean isCancelled() {
            return true;
        }
    }
}
