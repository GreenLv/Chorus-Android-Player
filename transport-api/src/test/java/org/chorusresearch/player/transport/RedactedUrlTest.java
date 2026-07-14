/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package org.chorusresearch.player.transport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.URI;
import org.junit.Test;

public class RedactedUrlTest {
    @Test
    public void removesInfrastructureAndQuery() {
        String result = RedactedUrl.describe(
                URI.create("https://private.example:9443/video/manifest.mpd?token=secret"));
        assertEquals("https://<redacted>/manifest.mpd", result);
        assertFalse(result.contains("private.example"));
        assertFalse(result.contains("secret"));
    }
}
