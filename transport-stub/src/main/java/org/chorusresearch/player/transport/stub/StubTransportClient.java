/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport.stub;

import org.chorusresearch.player.transport.Cancellable;
import org.chorusresearch.player.transport.TransportCallback;
import org.chorusresearch.player.transport.TransportCapabilities;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.TransportRequest;
import org.chorusresearch.player.transport.TransportResponse;

import java.io.IOException;

/** A deterministic unavailable provider for builds and tests without native code. */
public final class StubTransportClient implements TransportClient {
    private static final String MESSAGE =
            "No Chorus transport provider is installed. See docs/NATIVE_LIBRARY.md.";

    @Override
    public TransportCapabilities capabilities() {
        return new TransportCapabilities(1, false, false, false, false);
    }

    @Override
    public TransportResponse execute(TransportRequest request) throws IOException {
        throw new IOException(MESSAGE);
    }

    @Override
    public Cancellable enqueue(TransportRequest request, TransportCallback callback) {
        callback.onFailure(new IOException(MESSAGE));
        return new Cancellable() {
            @Override public void cancel() {}
            @Override public boolean isCancelled() { return true; }
        };
    }

    @Override
    public void close() {}
}
