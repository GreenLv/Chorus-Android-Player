/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TransportResponse {
    private final int statusCode;
    private final Map<String, List<String>> headers;
    private final byte[] body;
    private final long sentAtEpochMs;
    private final long receivedAtEpochMs;

    public TransportResponse(
            int statusCode,
            Map<String, List<String>> headers,
            byte[] body,
            long sentAtEpochMs,
            long receivedAtEpochMs) {
        this.statusCode = statusCode;
        this.headers = Collections.unmodifiableMap(new LinkedHashMap<>(headers));
        this.body = body.clone();
        this.sentAtEpochMs = sentAtEpochMs;
        this.receivedAtEpochMs = receivedAtEpochMs;
    }

    public int statusCode() { return statusCode; }
    public Map<String, List<String>> headers() { return headers; }
    public byte[] body() { return body.clone(); }
    public long sentAtEpochMs() { return sentAtEpochMs; }
    public long receivedAtEpochMs() { return receivedAtEpochMs; }

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    public String firstHeader(String name) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (entry.getKey() != null && entry.getKey().equalsIgnoreCase(name)
                    && entry.getValue() != null && !entry.getValue().isEmpty()) {
                return entry.getValue().get(0);
            }
        }
        return null;
    }
}
