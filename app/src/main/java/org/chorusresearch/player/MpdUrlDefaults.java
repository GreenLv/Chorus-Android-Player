/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player;

final class MpdUrlDefaults {
    private MpdUrlDefaults() {}

    static String initialValue(String savedRuntimeValue, String localBuildDefault) {
        if (savedRuntimeValue != null) return savedRuntimeValue;
        return localBuildDefault == null ? "" : localBuildDefault;
    }
}
