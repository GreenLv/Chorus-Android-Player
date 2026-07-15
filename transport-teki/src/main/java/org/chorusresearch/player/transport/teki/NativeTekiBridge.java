/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport.teki;

import org.chorusresearch.player.transport.ServerPathStats;
import org.chorusresearch.player.transport.ServerPathStatsListener;

/** Minimal JNI boundary; no XQUIC headers or implementation are linked here. */
final class NativeTekiBridge {
    static final int CAPABILITY_HTTP3 = 1;
    static final int CAPABILITY_MULTIPATH = 1 << 1;
    static final int CAPABILITY_CLIENT_QOE = 1 << 2;
    static final int CAPABILITY_SERVER_QOE = 1 << 3;

    private static final boolean LIBRARY_LOADED;

    static {
        boolean loaded;
        try {
            System.loadLibrary("chorus-teki-jni");
            loaded = true;
        } catch (LinkageError error) {
            loaded = false;
        }
        LIBRARY_LOADED = loaded;
    }

    private volatile ServerPathStatsListener listener;

    NativeTekiBridge() {
        if (LIBRARY_LOADED) nativeSetCallbackTarget(this);
    }

    int backendApiVersion() {
        return LIBRARY_LOADED ? nativeBackendApiVersion() : 0;
    }

    int capabilityFlags() {
        return LIBRARY_LOADED ? nativeCapabilityFlags() : 0;
    }

    boolean sendClientQoe(int chunkIndex, long expectedDeliveryTimeMs) {
        return LIBRARY_LOADED
                && nativeSendClientQoe(chunkIndex, expectedDeliveryTimeMs) == 0;
    }

    void setServerPathStatsListener(ServerPathStatsListener listener) {
        this.listener = listener;
    }

    void close() {
        listener = null;
        if (LIBRARY_LOADED) nativeClose();
    }

    @SuppressWarnings("unused") // Called from JNI when an authorized backend publishes QoE.
    private void onServerPathStats(
            long sequenceNumber,
            int fastPathIndex,
            int slowPathIndex,
            double fastPathReceiveMbps,
            double slowPathReceiveMbps,
            double fastPathRttMs,
            double slowPathRttMs,
            double fastPathRatio,
            long receivedAtElapsedMs) {
        ServerPathStatsListener current = listener;
        if (current == null) return;
        try {
            current.onServerPathStats(new ServerPathStats(
                    sequenceNumber,
                    fastPathIndex,
                    slowPathIndex,
                    fastPathReceiveMbps,
                    slowPathReceiveMbps,
                    fastPathRttMs,
                    slowPathRttMs,
                    fastPathRatio,
                    receivedAtElapsedMs));
        } catch (IllegalArgumentException ignored) {
            // Reject malformed provider data without crashing the player.
        }
    }

    private static native int nativeBackendApiVersion();
    private static native int nativeCapabilityFlags();
    private static native int nativeSendClientQoe(int chunkIndex, long expectedDeliveryTimeMs);
    private static native void nativeSetCallbackTarget(NativeTekiBridge target);
    private static native void nativeClose();
}
