/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport.teki;

import android.net.Network;
import android.os.ParcelFileDescriptor;

import java.io.IOException;
import java.util.Objects;

/** Binds a duplicate of a native socket descriptor to an Android Network. */
public final class NetworkSocketBinder {
    private NetworkSocketBinder() {}

    public static ParcelFileDescriptor bind(Network network, int socketFd) throws IOException {
        Objects.requireNonNull(network, "network");
        if (socketFd < 0) throw new IllegalArgumentException("socketFd must not be negative");
        ParcelFileDescriptor descriptor = ParcelFileDescriptor.fromFd(socketFd);
        try {
            network.bindSocket(descriptor.getFileDescriptor());
            return descriptor;
        } catch (IOException | RuntimeException exception) {
            descriptor.close();
            throw exception;
        }
    }
}
