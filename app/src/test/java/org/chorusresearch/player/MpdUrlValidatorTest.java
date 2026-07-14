/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package org.chorusresearch.player;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class MpdUrlValidatorTest {
    @Test
    public void acceptsCompleteHttpsMpdUrl() {
        assertEquals("example.invalid", MpdUrlValidator.requireHttpsMpd(
                "https://example.invalid/video/manifest.mpd").getHost());
    }

    @Test
    public void rejectsCleartextAndIncompleteUrls() {
        assertThrows(IllegalArgumentException.class,
                () -> MpdUrlValidator.requireHttpsMpd("http://example.invalid/manifest.mpd"));
        assertThrows(IllegalArgumentException.class,
                () -> MpdUrlValidator.requireHttpsMpd("https://example.invalid/video"));
    }
}
