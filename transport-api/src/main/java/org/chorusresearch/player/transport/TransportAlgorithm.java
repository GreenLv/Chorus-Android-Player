/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package org.chorusresearch.player.transport;

/** Algorithm names used in the paper and public transport API. */
public enum TransportAlgorithm {
    CHORUS("Chorus"),
    XLINK("XLINK"),
    MIN_RTT_RI("MinRTT+RI"),
    MIN_RTT("MinRTT"),
    SP("SP");

    private final String displayName;

    TransportAlgorithm(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
