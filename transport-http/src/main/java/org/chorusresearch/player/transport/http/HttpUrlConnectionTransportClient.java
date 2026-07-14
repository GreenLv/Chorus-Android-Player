/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport.http;

import org.chorusresearch.player.transport.Cancellable;
import org.chorusresearch.player.transport.RedactedUrl;
import org.chorusresearch.player.transport.TransportCallback;
import org.chorusresearch.player.transport.TransportCapabilities;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.TransportRequest;
import org.chorusresearch.player.transport.TransportResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/** Generic HTTP compatibility transport. It does not implement Chorus or HTTP/3. */
public final class HttpUrlConnectionTransportClient implements TransportClient {
    private static final int MAX_RESPONSE_BYTES = 128 * 1024 * 1024;
    private final int connectTimeoutMs;
    private final int readTimeoutMs;
    private final ExecutorService executor;
    private final AtomicBoolean closed = new AtomicBoolean();

    public HttpUrlConnectionTransportClient() {
        this(10_000, 30_000);
    }

    public HttpUrlConnectionTransportClient(int connectTimeoutMs, int readTimeoutMs) {
        if (connectTimeoutMs <= 0 || readTimeoutMs <= 0) {
            throw new IllegalArgumentException("timeouts must be positive");
        }
        this.connectTimeoutMs = connectTimeoutMs;
        this.readTimeoutMs = readTimeoutMs;
        this.executor = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "chorus-http-transport");
            thread.setDaemon(true);
            return thread;
        });
    }

    @Override
    public TransportCapabilities capabilities() {
        return new TransportCapabilities(1, false, false, false, false);
    }

    @Override
    public TransportResponse execute(TransportRequest request) throws IOException {
        Objects.requireNonNull(request, "request");
        if (closed.get()) throw new IOException("transport client is closed");

        HttpURLConnection connection = null;
        long sentAtMs = System.currentTimeMillis();
        try {
            connection = (HttpURLConnection) request.uri().toURL().openConnection();
            connection.setConnectTimeout(connectTimeoutMs);
            connection.setReadTimeout(readTimeoutMs);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod(request.method());
            for (Map.Entry<String, String> header : request.headers().entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }

            byte[] requestBody = request.body();
            if (requestBody.length > 0) {
                connection.setDoOutput(true);
                try (OutputStream output = connection.getOutputStream()) {
                    output.write(requestBody);
                }
            }

            int status = connection.getResponseCode();
            InputStream stream = status >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();
            byte[] body = stream == null ? new byte[0] : readAll(stream);
            Map<String, List<String>> headers = connection.getHeaderFields();
            if (headers == null) headers = Collections.emptyMap();
            return new TransportResponse(
                    status, headers, body, sentAtMs, System.currentTimeMillis());
        } catch (IOException exception) {
            throw new IOException(
                    "request failed for " + RedactedUrl.describe(request.uri())
                            + " (" + exception.getClass().getSimpleName() + ")");
        } finally {
            if (connection != null) connection.disconnect();
        }
    }

    private static byte[] readAll(InputStream input) throws IOException {
        try (InputStream stream = input;
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[16 * 1024];
            int count;
            int total = 0;
            while ((count = stream.read(buffer)) != -1) {
                total += count;
                if (total > MAX_RESPONSE_BYTES) {
                    throw new IOException("response exceeds 128 MiB safety limit");
                }
                output.write(buffer, 0, count);
            }
            return output.toByteArray();
        }
    }

    @Override
    public Cancellable enqueue(TransportRequest request, TransportCallback callback) {
        Objects.requireNonNull(callback, "callback");
        Future<?> future = executor.submit(() -> {
            try {
                callback.onResponse(execute(request));
            } catch (IOException exception) {
                callback.onFailure(exception);
            }
        });
        return new FutureCancellable(future);
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            executor.shutdownNow();
        }
    }

    private static final class FutureCancellable implements Cancellable {
        private final Future<?> future;

        private FutureCancellable(Future<?> future) {
            this.future = future;
        }

        @Override
        public void cancel() {
            future.cancel(true);
        }

        @Override
        public boolean isCancelled() {
            return future.isCancelled();
        }
    }
}
