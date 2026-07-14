/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package org.chorusresearch.player.transport;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TransportAlgorithmTest {
    @Test
    public void nativeIdsRemainStable() {
        assertEquals(TransportAlgorithm.CHORUS, TransportAlgorithm.fromNativeId(0));
        assertEquals(TransportAlgorithm.XLINK, TransportAlgorithm.fromNativeId(1));
        assertEquals(TransportAlgorithm.MIN_RTT_RI, TransportAlgorithm.fromNativeId(2));
        assertEquals(TransportAlgorithm.MIN_RTT, TransportAlgorithm.fromNativeId(3));
        assertEquals(TransportAlgorithm.SP, TransportAlgorithm.fromNativeId(4));
        assertEquals("MinRTTRI", TransportAlgorithm.MIN_RTT_RI.slug());
    }
}
