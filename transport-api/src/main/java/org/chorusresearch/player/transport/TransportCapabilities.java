/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

import java.util.Objects;

public final class TransportCapabilities {
    public static final int UNAVAILABLE_API_VERSION = 0;
    public static final int CURRENT_API_VERSION = 1;

    private final int apiVersion;
    private final boolean http3;
    private final boolean multipath;
    private final boolean clientQoe;
    private final boolean serverQoe;

    public TransportCapabilities(
            int apiVersion,
            boolean http3,
            boolean multipath,
            boolean clientQoe,
            boolean serverQoe) {
        if (apiVersion < UNAVAILABLE_API_VERSION) {
            throw new IllegalArgumentException("apiVersion must not be negative");
        }
        this.apiVersion = apiVersion;
        this.http3 = http3;
        this.multipath = multipath;
        this.clientQoe = clientQoe;
        this.serverQoe = serverQoe;
    }

    public int apiVersion() {
        return apiVersion;
    }

    public boolean supportsHttp3() {
        return http3;
    }

    public boolean supportsMultipath() {
        return multipath;
    }

    public boolean supportsClientQoe() {
        return clientQoe;
    }

    public boolean supportsServerQoe() {
        return serverQoe;
    }

    public void requireChorus() {
        if (apiVersion != CURRENT_API_VERSION
                || !http3 || !multipath || !clientQoe || !serverQoe) {
            throw new IllegalStateException(
                    "provider does not implement Chorus transport API v" + CURRENT_API_VERSION);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof TransportCapabilities)) return false;
        TransportCapabilities that = (TransportCapabilities) other;
        return apiVersion == that.apiVersion
                && http3 == that.http3
                && multipath == that.multipath
                && clientQoe == that.clientQoe
                && serverQoe == that.serverQoe;
    }

    @Override
    public int hashCode() {
        return Objects.hash(apiVersion, http3, multipath, clientQoe, serverQoe);
    }
}
