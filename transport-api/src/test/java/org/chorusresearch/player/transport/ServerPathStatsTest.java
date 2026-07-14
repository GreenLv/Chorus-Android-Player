/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package org.chorusresearch.player.transport;

import org.junit.Test;

public class ServerPathStatsTest {
    @Test(expected = IllegalArgumentException.class)
    public void rejectsMalformedPathIndexes() {
        new ServerPathStats(1, 0, 0, 1, 1, 10, 10, 0.5, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNegativeRtt() {
        new ServerPathStats(1, 0, 1, 1, 1, -1, 10, 0.5, 1);
    }
}
