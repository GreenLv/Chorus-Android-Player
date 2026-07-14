/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package org.chorusresearch.player.transport.stub;

import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.net.URI;
import org.chorusresearch.player.transport.TransportRequest;
import org.junit.Test;

public class StubTransportClientTest {
    @Test
    public void failsWithActionableMessage() {
        StubTransportClient client = new StubTransportClient();
        IOException error = assertThrows(IOException.class, () -> client.execute(
                TransportRequest.get(URI.create("https://example.invalid/manifest.mpd"))
                        .build()));
        org.junit.Assert.assertTrue(error.getMessage().contains("NATIVE_LIBRARY.md"));
    }
}
