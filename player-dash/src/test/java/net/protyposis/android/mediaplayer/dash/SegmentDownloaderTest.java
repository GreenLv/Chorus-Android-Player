/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package net.protyposis.android.mediaplayer.dash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;

import org.chorusresearch.player.transport.Cancellable;
import org.chorusresearch.player.transport.ServerPathStats;
import org.chorusresearch.player.transport.ServerPathStatsListener;
import org.chorusresearch.player.transport.TransportAlgorithm;
import org.chorusresearch.player.transport.TransportCallback;
import org.chorusresearch.player.transport.TransportCapabilities;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.TransportRequest;
import org.chorusresearch.player.transport.TransportResponse;
import org.junit.Test;

public class SegmentDownloaderTest {
    @Test
    public void buildsQoeBeforeRequestAndHandlesSynchronousCallback() {
        List<String> events = new ArrayList<>();
        RecordingClient client = new RecordingClient(events);
        ChorusAdaptationLogic logic = new ChorusAdaptationLogic(
                client, (representation, index) -> OptionalLong.of(2_000_000));

        AdaptationSet adaptationSet = new AdaptationSet();
        adaptationSet.group = 7;
        Representation representation = new Representation();
        representation.bandwidth = 4_000_000;
        representation.segmentDurationUs = 4_000_000;
        adaptationSet.representations.add(representation);

        logic.reportSegmentDownload(adaptationSet, representation, null, 0, 1_000_000, 1_000);
        events.add("decision");
        logic.getRecommendedRepresentation(adaptationSet, 3);

        CachedSegment segment = new CachedSegment(
                3,
                new Segment("https://example.invalid/video/segment.m4s"),
                representation,
                adaptationSet);
        SegmentDownloader downloader = new SegmentDownloader(
                client, Collections.emptyMap(), logic);
        downloader.downloadAsync(segment, new SegmentDownloader.SegmentDownloadCallback() {
            @Override
            public void onFailure(CachedSegment ignored, IOException exception) {
                throw new AssertionError(exception);
            }

            @Override
            public void onSuccess(SegmentDownloader.DownloadFinishedArgs ignored) {
                events.add("response");
            }
        });

        assertEquals(Arrays.asList("decision", "request", "server-qoe", "response"), events);
        assertFalse(downloader.isDownloading(adaptationSet, 3));
    }

    private static final class RecordingClient implements TransportClient {
        private final List<String> events;
        private ServerPathStatsListener listener;

        RecordingClient(List<String> events) {
            this.events = events;
        }

        @Override public TransportCapabilities capabilities() {
            return new TransportCapabilities(1, true, true, true, true);
        }

        @Override public TransportResponse execute(TransportRequest request) {
            throw new UnsupportedOperationException();
        }

        @Override public Cancellable enqueue(TransportRequest request, TransportCallback callback) {
            assertEquals(3, request.chunkIndex());
            assertEquals(2_000, request.expectedDeliveryTimeMs());
            assertEquals(TransportAlgorithm.CHORUS, request.algorithm());
            events.add("request");
            listener.onServerPathStats(new ServerPathStats(
                    1, 0, 1, 8, 2, 20, 50, 0.8, 1));
            events.add("server-qoe");
            callback.onResponse(new TransportResponse(
                    200, Collections.emptyMap(), new byte[] {1}, 1, 2));
            return new Cancellable() {
                @Override public void cancel() {}
                @Override public boolean isCancelled() { return false; }
            };
        }

        @Override public Optional<ServerPathStats> latestServerPathStats() {
            return Optional.empty();
        }

        @Override public void setServerPathStatsListener(ServerPathStatsListener listener) {
            this.listener = listener;
        }

        @Override public void close() {}
    }
}
