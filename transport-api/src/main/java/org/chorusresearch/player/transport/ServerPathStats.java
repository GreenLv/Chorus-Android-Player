/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

public final class ServerPathStats {
    private final long sequenceNumber;
    private final int fastPathIndex;
    private final int slowPathIndex;
    private final double fastPathReceiveMbps;
    private final double slowPathReceiveMbps;
    private final double fastPathRttMs;
    private final double slowPathRttMs;
    private final double fastPathRatio;
    private final long receivedAtElapsedMs;

    public ServerPathStats(
            long sequenceNumber,
            int fastPathIndex,
            int slowPathIndex,
            double fastPathReceiveMbps,
            double slowPathReceiveMbps,
            double fastPathRttMs,
            double slowPathRttMs,
            double fastPathRatio,
            long receivedAtElapsedMs) {
        if (sequenceNumber < 0) {
            throw new IllegalArgumentException("sequence number must not be negative");
        }
        if (fastPathIndex < 0 || slowPathIndex < 0 || fastPathIndex == slowPathIndex) {
            throw new IllegalArgumentException("path indexes must be distinct and non-negative");
        }
        if (fastPathReceiveMbps < 0 || slowPathReceiveMbps < 0) {
            throw new IllegalArgumentException("path receive rates must not be negative");
        }
        if (fastPathRttMs < 0 || slowPathRttMs < 0) {
            throw new IllegalArgumentException("path RTT values must not be negative");
        }
        if (fastPathRatio < 0 || fastPathRatio > 1) {
            throw new IllegalArgumentException("fastPathRatio must be in [0, 1]");
        }
        if (receivedAtElapsedMs < 0) {
            throw new IllegalArgumentException("receive timestamp must not be negative");
        }
        this.sequenceNumber = sequenceNumber;
        this.fastPathIndex = fastPathIndex;
        this.slowPathIndex = slowPathIndex;
        this.fastPathReceiveMbps = fastPathReceiveMbps;
        this.slowPathReceiveMbps = slowPathReceiveMbps;
        this.fastPathRttMs = fastPathRttMs;
        this.slowPathRttMs = slowPathRttMs;
        this.fastPathRatio = fastPathRatio;
        this.receivedAtElapsedMs = receivedAtElapsedMs;
    }

    public long sequenceNumber() { return sequenceNumber; }
    public int fastPathIndex() { return fastPathIndex; }
    public int slowPathIndex() { return slowPathIndex; }
    public double fastPathReceiveMbps() { return fastPathReceiveMbps; }
    public double slowPathReceiveMbps() { return slowPathReceiveMbps; }
    public double fastPathRttMs() { return fastPathRttMs; }
    public double slowPathRttMs() { return slowPathRttMs; }
    public double fastPathRatio() { return fastPathRatio; }
    public long receivedAtElapsedMs() { return receivedAtElapsedMs; }
}
