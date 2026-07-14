/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package org.chorusresearch.player.transport;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TransportAlgorithmTest {
    @Test
    public void usesPaperFacingDisplayNames() {
        assertEquals("Chorus", TransportAlgorithm.CHORUS.displayName());
        assertEquals("XLINK", TransportAlgorithm.XLINK.displayName());
        assertEquals("MinRTT+RI", TransportAlgorithm.MIN_RTT_RI.displayName());
        assertEquals("MinRTT", TransportAlgorithm.MIN_RTT.displayName());
        assertEquals("SP", TransportAlgorithm.SP.displayName());
    }
}
