/*
 * Copyright 2016 Mario Guggenberger <mg@protyposis.net>
 * Modifications Copyright 2026 Gerui Lv and Qingyue Tan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 */
package net.protyposis.android.mediaplayer.dash;

import android.os.SystemClock;
import android.util.Log;

import org.chorusresearch.player.transport.Cancellable;
import org.chorusresearch.player.transport.RedactedUrl;
import org.chorusresearch.player.transport.TransportCallback;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.TransportRequest;
import org.chorusresearch.player.transport.TransportResponse;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/** Downloads DASH segments through the public transport provider contract. */
public class SegmentDownloader {
    private static final String TAG = SegmentDownloader.class.getSimpleName();
    static final int INITSEGMENT = -1;

    private final TransportClient transportClient;
    private final AdaptationLogic adaptationLogic;
    private final Map<String, String> headers;
    private final PriorityQueue<DownloadQueueItem> downloadQueue;
    private final Map<String, Cancellable> activeDownloads;
    private int maxConcurrentDownloads = 3;

    public SegmentDownloader(
            TransportClient transportClient,
            Map<String, String> headers,
            AdaptationLogic adaptationLogic) {
        if (transportClient == null) {
            throw new IllegalArgumentException("transportClient must be set");
        }
        this.transportClient = transportClient;
        this.adaptationLogic = adaptationLogic;
        this.headers = headers == null ? new HashMap<>() : new HashMap<>(headers);
        this.downloadQueue = new PriorityQueue<>(20, Comparator.comparingLong(item ->
                item.segment.number * item.segment.representation.segmentDurationUs));
        this.activeDownloads = new HashMap<>();
    }

    byte[] downloadBlocking(Segment segment, int segmentNumber) throws IOException {
        TransportRequest request = buildSegmentRequest(segment, null, segmentNumber);
        return executeBlocking(request, segmentNumber);
    }

    byte[] downloadBlocking(CachedSegment segment) throws IOException {
        TransportRequest request = buildSegmentRequest(
                segment.segment, segment.representation, segment.number);
        return executeBlocking(request, segment.number);
    }

    private byte[] executeBlocking(TransportRequest request, int segmentNumber) throws IOException {
        TransportResponse response = transportClient.execute(request);
        if (!response.isSuccessful()) {
            throw new IOException("segment request failed with HTTP " + response.statusCode()
                    + " at segment " + segmentNumber);
        }
        return response.body();
    }

    synchronized void downloadAsync(CachedSegment segment, SegmentDownloadCallback callback) {
        downloadQueue.offer(new DownloadQueueItem(segment, callback));
        scheduleDownloads();
    }

    synchronized boolean isDownloading(AdaptationSet adaptationSet, int segmentNumber) {
        if (activeDownloads.containsKey(key(adaptationSet, segmentNumber))) {
            return true;
        }
        for (DownloadQueueItem item : downloadQueue) {
            if (item.segment.number == segmentNumber
                    && item.segment.adaptationSet == adaptationSet) {
                return true;
            }
        }
        return false;
    }

    synchronized void cancelDownloads(AdaptationSet adaptationSet) {
        List<DownloadQueueItem> queued = new ArrayList<>();
        for (DownloadQueueItem item : downloadQueue) {
            if (item.segment.adaptationSet == adaptationSet) queued.add(item);
        }
        downloadQueue.removeAll(queued);

        List<String> activeKeys = new ArrayList<>();
        String prefix = adaptationSet.group + "-";
        for (Map.Entry<String, Cancellable> entry : activeDownloads.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                entry.getValue().cancel();
                activeKeys.add(entry.getKey());
            }
        }
        activeKeys.forEach(activeDownloads::remove);
    }

    private synchronized void scheduleDownloads() {
        int available = maxConcurrentDownloads - activeDownloads.size();
        for (int i = 0; i < available && !downloadQueue.isEmpty(); i++) {
            DownloadQueueItem item = downloadQueue.poll();
            String requestKey = key(item.segment.adaptationSet, item.segment.number);
            final TransportRequest request;
            try {
                request = buildSegmentRequest(item.segment.segment,
                        item.segment.representation, item.segment.number);
            } catch (RuntimeException error) {
                item.callback.onFailure(item.segment,
                        new IOException("invalid segment URL"));
                continue;
            }

            long startMs = SystemClock.elapsedRealtime();
            PendingCancellable pending = new PendingCancellable();
            activeDownloads.put(requestKey, pending);
            final Cancellable cancellable;
            try {
                cancellable = transportClient.enqueue(request, new TransportCallback() {
                @Override
                public void onResponse(TransportResponse response) {
                    synchronized (SegmentDownloader.this) {
                        activeDownloads.remove(requestKey);
                    }
                    if (!response.isSuccessful()) {
                        item.callback.onFailure(item.segment, new IOException(
                                "segment request failed with HTTP " + response.statusCode()
                                        + " for " + RedactedUrl.describe(request.uri())));
                    } else {
                        try {
                            item.callback.onSuccess(new DownloadFinishedArgs(
                                    item.segment,
                                    response.body(),
                                    SystemClock.elapsedRealtime() - startMs));
                        } catch (IOException error) {
                            item.callback.onFailure(item.segment, error);
                        }
                    }
                    scheduleDownloads();
                }

                @Override
                public void onFailure(IOException exception) {
                    synchronized (SegmentDownloader.this) {
                        activeDownloads.remove(requestKey);
                    }
                    item.callback.onFailure(item.segment, exception);
                    scheduleDownloads();
                }
                });
            } catch (RuntimeException error) {
                activeDownloads.remove(requestKey);
                item.callback.onFailure(item.segment,
                        new IOException("transport rejected segment request ("
                                + error.getClass().getSimpleName() + ")"));
                continue;
            }
            pending.setDelegate(cancellable);
        }
    }

    private String key(AdaptationSet adaptationSet, int segmentNumber) {
        return adaptationSet.group + "-" + segmentNumber;
    }

    private TransportRequest buildSegmentRequest(
            Segment segment,
            Representation representation,
            int segmentNumber) {
        String url = segment.media.replace(" ", "%20").replace("^", "%5E");
        TransportRequest.Builder builder = TransportRequest.get(URI.create(url)).headers(headers);
        if (segment.hasRange()) {
            builder.header("Range", "bytes=" + segment.range);
        }
        if (representation != null
                && segmentNumber >= 0
                && adaptationLogic instanceof TransportRequestContextProvider) {
            ((TransportRequestContextProvider) adaptationLogic).addRequestContext(
                    builder, representation, segmentNumber);
        }
        return builder.build();
    }

    private static final class PendingCancellable implements Cancellable {
        private Cancellable delegate;
        private boolean cancelled;

        synchronized void setDelegate(Cancellable delegate) {
            this.delegate = delegate;
            if (cancelled && delegate != null) delegate.cancel();
        }

        @Override
        public synchronized void cancel() {
            cancelled = true;
            if (delegate != null) delegate.cancel();
        }

        @Override
        public synchronized boolean isCancelled() {
            return cancelled || (delegate != null && delegate.isCancelled());
        }
    }

    static final class DownloadFinishedArgs {
        final CachedSegment cachedSegment;
        final byte[] data;
        final long duration;

        DownloadFinishedArgs(CachedSegment cachedSegment, byte[] data, long duration) {
            this.cachedSegment = cachedSegment;
            this.data = data;
            this.duration = duration;
        }
    }

    interface SegmentDownloadCallback {
        void onFailure(CachedSegment cachedSegment, IOException exception);
        void onSuccess(DownloadFinishedArgs args) throws IOException;
    }

    private static final class DownloadQueueItem {
        final CachedSegment segment;
        final SegmentDownloadCallback callback;

        DownloadQueueItem(CachedSegment segment, SegmentDownloadCallback callback) {
            this.segment = segment;
            this.callback = callback;
        }
    }
}
