/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package org.chorusresearch.player;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MpdUrlDefaultsTest {
    @Test
    public void savedRuntimeInputOverridesLocalBuildDefault() {
        assertEquals("https://example.invalid/runtime.mpd",
                MpdUrlDefaults.initialValue(
                        "https://example.invalid/runtime.mpd",
                        "https://example.invalid/build.mpd"));
    }

    @Test
    public void localBuildDefaultIsUsedOnlyWithoutSavedInput() {
        assertEquals("https://example.invalid/build.mpd",
                MpdUrlDefaults.initialValue(
                        null, "https://example.invalid/build.mpd"));
        assertEquals("", MpdUrlDefaults.initialValue("", "ignored"));
    }
}
