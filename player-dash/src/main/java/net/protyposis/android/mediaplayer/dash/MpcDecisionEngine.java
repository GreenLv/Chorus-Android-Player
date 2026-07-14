/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package net.protyposis.android.mediaplayer.dash;

/** Deterministic finite-horizon MPC evaluator for the published paper preset. */
public final class MpcDecisionEngine {
    private final int horizon;
    private final double chunkDurationSeconds;
    private final double rebufferPenalty;
    private final double smoothnessPenalty;

    public MpcDecisionEngine(
            int horizon,
            double chunkDurationSeconds,
            double rebufferPenalty,
            double smoothnessPenalty) {
        if (horizon <= 0 || chunkDurationSeconds <= 0) {
            throw new IllegalArgumentException("horizon and chunk duration must be positive");
        }
        this.horizon = horizon;
        this.chunkDurationSeconds = chunkDurationSeconds;
        this.rebufferPenalty = rebufferPenalty;
        this.smoothnessPenalty = smoothnessPenalty;
    }

    public long combinationCount(int qualityLevels) {
        if (qualityLevels <= 0) throw new IllegalArgumentException("qualityLevels must be positive");
        long count = 1;
        for (int i = 0; i < horizon; i++) count = Math.multiplyExact(count, qualityLevels);
        return count;
    }

    public int chooseFirstQuality(
            double[] bitratesMbps,
            long[][] segmentSizesBytes,
            double predictedThroughputMbps,
            double bufferSeconds,
            int previousQuality) {
        if (bitratesMbps.length == 0 || segmentSizesBytes.length != bitratesMbps.length) {
            throw new IllegalArgumentException("bitrate and segment-size dimensions differ");
        }
        if (predictedThroughputMbps <= 0 || previousQuality < 0
                || previousQuality >= bitratesMbps.length) {
            return 0;
        }
        for (long[] qualitySizes : segmentSizesBytes) {
            if (qualitySizes.length < horizon) {
                throw new IllegalArgumentException("each quality needs one size per horizon step");
            }
        }

        SearchState state = new SearchState();
        enumerate(0, new int[horizon], bitratesMbps, segmentSizesBytes,
                predictedThroughputMbps, bufferSeconds, previousQuality, state);
        return state.bestFirstQuality;
    }

    private void enumerate(
            int depth,
            int[] choices,
            double[] bitratesMbps,
            long[][] sizesBytes,
            double throughputMbps,
            double initialBufferSeconds,
            int previousQuality,
            SearchState state) {
        if (depth == horizon) {
            double score = score(choices, bitratesMbps, sizesBytes, throughputMbps,
                    initialBufferSeconds, previousQuality);
            if (score > state.bestScore) {
                state.bestScore = score;
                state.bestFirstQuality = choices[0];
            }
            return;
        }
        for (int quality = 0; quality < bitratesMbps.length; quality++) {
            choices[depth] = quality;
            enumerate(depth + 1, choices, bitratesMbps, sizesBytes, throughputMbps,
                    initialBufferSeconds, previousQuality, state);
        }
    }

    private double score(
            int[] choices,
            double[] bitratesMbps,
            long[][] sizesBytes,
            double throughputMbps,
            double bufferSeconds,
            int previousQuality) {
        double score = 0;
        int lastQuality = previousQuality;
        for (int step = 0; step < horizon; step++) {
            int quality = choices[step];
            double downloadSeconds = sizesBytes[quality][step] * 8.0
                    / (throughputMbps * 1_000_000.0);
            double rebufferSeconds = Math.max(downloadSeconds - bufferSeconds, 0);
            bufferSeconds = Math.max(bufferSeconds - downloadSeconds, 0)
                    + chunkDurationSeconds;
            score += bitratesMbps[quality]
                    - rebufferPenalty * rebufferSeconds
                    - smoothnessPenalty * Math.abs(
                            bitratesMbps[quality] - bitratesMbps[lastQuality]);
            lastQuality = quality;
        }
        return score;
    }

    private static final class SearchState {
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestFirstQuality;
    }
}
