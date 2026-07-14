/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package org.chorusresearch.player.transport;

/** Public paper names with stable legacy native identifiers. */
public enum TransportAlgorithm {
    CHORUS(0, "Chorus", "Chorus"),
    XLINK(1, "XLINK", "XLINK"),
    MIN_RTT_RI(2, "MinRTT+RI", "MinRTTRI"),
    MIN_RTT(3, "MinRTT", "MinRTT"),
    SP(4, "SP", "SP");

    private final int nativeId;
    private final String displayName;
    private final String slug;

    TransportAlgorithm(int nativeId, String displayName, String slug) {
        this.nativeId = nativeId;
        this.displayName = displayName;
        this.slug = slug;
    }

    public int nativeId() {
        return nativeId;
    }

    public String displayName() {
        return displayName;
    }

    public String slug() {
        return slug;
    }

    public static TransportAlgorithm fromNativeId(int nativeId) {
        for (TransportAlgorithm algorithm : values()) {
            if (algorithm.nativeId == nativeId) {
                return algorithm;
            }
        }
        throw new IllegalArgumentException("unknown transport algorithm id: " + nativeId);
    }
}
