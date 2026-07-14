/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

/** Receives validated, provider-originated server path statistics. */
@FunctionalInterface
public interface ServerPathStatsListener {
    void onServerPathStats(ServerPathStats stats);
}
