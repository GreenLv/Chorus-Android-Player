/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package net.protyposis.android.mediaplayer.dash;

import android.os.SystemClock;

import org.chorusresearch.player.transport.ServerPathStats;
import org.chorusresearch.player.transport.TransportAlgorithm;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.TransportRequest;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.function.LongSupplier;

/**
 * Public player-side predictor described by Chorus. A provider without fresh server QoE data
 * deterministically falls back to the harmonic mean of the last five segment throughputs.
 */
public final class ChorusAdaptationLogic implements AdaptationLogic, TransportRequestContextProvider {
    public static final int HM_SAMPLE_COUNT = 5;
    public static final long DEFAULT_SERVER_STATS_MAX_AGE_MS = 1_000;
    private static final double SAFETY_FACTOR = 0.90;

    private final TransportClient transportClient;
    private final LongSupplier elapsedRealtimeMs;
    private final long serverStatsMaxAgeMs;
    private final SegmentSizeProvider segmentSizeProvider;
    private final Deque<Double> throughputHistoryMbps = new ArrayDeque<>();
    private volatile ServerPathStats callbackStats;
    private double lastPredictionMbps;

    public ChorusAdaptationLogic(TransportClient transportClient) {
        this(transportClient, (representation, segmentIndex) -> OptionalLong.empty());
    }

    public ChorusAdaptationLogic(
            TransportClient transportClient,
            SegmentSizeProvider segmentSizeProvider) {
        this(transportClient, segmentSizeProvider, SystemClock::elapsedRealtime,
                DEFAULT_SERVER_STATS_MAX_AGE_MS);
    }

    ChorusAdaptationLogic(
            TransportClient transportClient,
            SegmentSizeProvider segmentSizeProvider,
            LongSupplier elapsedRealtimeMs,
            long serverStatsMaxAgeMs) {
        this.transportClient = transportClient;
        this.segmentSizeProvider = segmentSizeProvider;
        this.elapsedRealtimeMs = elapsedRealtimeMs;
        this.serverStatsMaxAgeMs = serverStatsMaxAgeMs;
        transportClient.setServerPathStatsListener(stats -> callbackStats = stats);
    }

    @Override
    public Representation initialize(AdaptationSet adaptationSet) {
        sortRepresentations(adaptationSet);
        return adaptationSet.representations.get(0);
    }

    @Override
    public void reportSegmentDownload(
            AdaptationSet adaptationSet,
            Representation representation,
            Segment segment,
            int segmentIndex,
            int byteSize,
            long downloadTimeMs) {
        if (byteSize <= 0 || downloadTimeMs <= 0) return;
        double throughputMbps = byteSize * 8.0 / (downloadTimeMs * 1_000.0);
        if (throughputHistoryMbps.size() == HM_SAMPLE_COUNT) {
            throughputHistoryMbps.removeFirst();
        }
        throughputHistoryMbps.addLast(throughputMbps);
    }

    @Override
    public Representation getRecommendedRepresentation(
            AdaptationSet adaptationSet, int segmentIndex) {
        sortRepresentations(adaptationSet);
        double predictionMbps = currentPredictionMbps();
        lastPredictionMbps = predictionMbps;
        if (predictionMbps <= 0) return adaptationSet.representations.get(0);

        double safeBitsPerSecond = predictionMbps * SAFETY_FACTOR * 1_000_000.0;
        Representation selected = adaptationSet.representations.get(0);
        for (Representation representation : adaptationSet.representations) {
            if (representation.bandwidth <= safeBitsPerSecond) selected = representation;
            else break;
        }
        return selected;
    }

    @Override
    public void addRequestContext(
            TransportRequest.Builder request,
            Representation representation,
            int segmentIndex) {
        if (lastPredictionMbps <= 0 || segmentIndex < 0) return;

        long estimatedSizeBytes = segmentSizeProvider.sizeBytes(representation, segmentIndex)
                .orElseGet(() -> estimateSizeFromManifest(representation));
        if (estimatedSizeBytes <= 0) return;

        long expectedTimeMs = Math.max(1L, Math.round(
                estimatedSizeBytes * 8.0 / (lastPredictionMbps * 1_000.0)));
        request.chunkContext(segmentIndex, expectedTimeMs, TransportAlgorithm.CHORUS);
    }

    double currentPredictionMbps() {
        Optional<ServerPathStats> latest = callbackStats == null
                ? transportClient.latestServerPathStats()
                : Optional.of(callbackStats);
        if (latest.isPresent()) {
            ServerPathStats stats = latest.get();
            long ageMs = elapsedRealtimeMs.getAsLong() - stats.receivedAtElapsedMs();
            if (ageMs >= 0 && ageMs <= serverStatsMaxAgeMs) {
                return predictMultipathMbps(stats);
            }
        }
        return harmonicMean(new ArrayList<>(throughputHistoryMbps));
    }

    public static double predictMultipathMbps(ServerPathStats stats) {
        double alpha = stats.fastPathRatio();
        double fast = stats.fastPathReceiveMbps();
        double slow = stats.slowPathReceiveMbps();
        double fastBound = alpha == 0 ? Double.POSITIVE_INFINITY : fast / alpha;
        double slowBound = alpha == 1 ? Double.POSITIVE_INFINITY : slow / (1 - alpha);
        double prediction = Math.min(fastBound, slowBound);
        return Math.max(prediction, Math.max(fast, slow));
    }

    static double harmonicMean(List<Double> samples) {
        if (samples.isEmpty()) return 0;
        double reciprocalSum = 0;
        for (double sample : samples) {
            if (sample <= 0) return 0;
            reciprocalSum += 1.0 / sample;
        }
        return samples.size() / reciprocalSum;
    }

    private static long estimateSizeFromManifest(Representation representation) {
        if (representation == null
                || representation.bandwidth <= 0
                || representation.segmentDurationUs <= 0) {
            return -1;
        }
        return Math.round(representation.bandwidth
                * (representation.segmentDurationUs / 1_000_000.0) / 8.0);
    }

    private static void sortRepresentations(AdaptationSet adaptationSet) {
        if (adaptationSet.representations.isEmpty()) {
            throw new IllegalArgumentException("adaptation set must not be empty");
        }
        adaptationSet.representations.sort(Comparator.comparingInt(value -> value.bandwidth));
    }
}
