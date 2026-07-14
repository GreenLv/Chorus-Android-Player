/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Locale;

public final class TransportRequest {
    public static final int NO_CHUNK = -1;
    public static final long UNKNOWN_EXPECTED_TIME_MS = -1;

    private final URI uri;
    private final String method;
    private final Map<String, String> headers;
    private final byte[] body;
    private final int chunkIndex;
    private final long expectedDeliveryTimeMs;
    private final TransportAlgorithm algorithm;

    private TransportRequest(Builder builder) {
        uri = builder.uri;
        method = builder.method;
        headers = Collections.unmodifiableMap(new LinkedHashMap<>(builder.headers));
        body = builder.body.clone();
        chunkIndex = builder.chunkIndex;
        expectedDeliveryTimeMs = builder.expectedDeliveryTimeMs;
        algorithm = builder.algorithm;
    }

    public URI uri() { return uri; }
    public String method() { return method; }
    public Map<String, String> headers() { return headers; }
    public byte[] body() { return body.clone(); }
    public int chunkIndex() { return chunkIndex; }
    public long expectedDeliveryTimeMs() { return expectedDeliveryTimeMs; }
    public TransportAlgorithm algorithm() { return algorithm; }

    public static Builder get(URI uri) {
        return new Builder(uri).method("GET");
    }

    public static final class Builder {
        private final URI uri;
        private String method = "GET";
        private final Map<String, String> headers = new LinkedHashMap<>();
        private byte[] body = new byte[0];
        private int chunkIndex = NO_CHUNK;
        private long expectedDeliveryTimeMs = UNKNOWN_EXPECTED_TIME_MS;
        private TransportAlgorithm algorithm = TransportAlgorithm.CHORUS;

        public Builder(URI uri) {
            this.uri = Objects.requireNonNull(uri, "uri");
            String scheme = uri.getScheme();
            if (scheme == null || !(scheme.equalsIgnoreCase("http")
                    || scheme.equalsIgnoreCase("https"))) {
                throw new IllegalArgumentException("only http and https URIs are supported");
            }
            if (uri.getHost() == null || uri.getHost().isEmpty()) {
                throw new IllegalArgumentException("URI must include a host");
            }
        }

        public Builder method(String method) {
            this.method = Objects.requireNonNull(method, "method").toUpperCase(Locale.ROOT);
            return this;
        }

        public Builder header(String name, String value) {
            headers.put(Objects.requireNonNull(name, "name"),
                    Objects.requireNonNull(value, "value"));
            return this;
        }

        public Builder headers(Map<String, String> values) {
            if (values != null) {
                values.forEach(this::header);
            }
            return this;
        }

        public Builder body(byte[] body) {
            this.body = Objects.requireNonNull(body, "body").clone();
            return this;
        }

        public Builder chunkContext(
                int chunkIndex,
                long expectedDeliveryTimeMs,
                TransportAlgorithm algorithm) {
            if (chunkIndex < 0) {
                throw new IllegalArgumentException("chunkIndex must not be negative");
            }
            if (expectedDeliveryTimeMs < 0) {
                throw new IllegalArgumentException("expectedDeliveryTimeMs must not be negative");
            }
            this.chunkIndex = chunkIndex;
            this.expectedDeliveryTimeMs = expectedDeliveryTimeMs;
            this.algorithm = Objects.requireNonNull(algorithm, "algorithm");
            return this;
        }

        public TransportRequest build() {
            return new TransportRequest(this);
        }
    }
}
