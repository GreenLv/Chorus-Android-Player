/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package net.protyposis.android.mediaplayer.dash;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MpcDecisionEngineTest {
    @Test
    public void paperPresetEnumerates3125Combinations() {
        MpcDecisionEngine engine = new MpcDecisionEngine(5, 4, 16, 1);
        assertEquals(3125, engine.combinationCount(5));
    }

    @Test
    public void choosesLowestQualityWhenPredictionIsUnavailable() {
        MpcDecisionEngine engine = new MpcDecisionEngine(5, 4, 16, 1);
        double[] bitrates = {1, 2.5, 5, 8, 16};
        long[][] sizes = new long[5][5];
        assertEquals(0, engine.chooseFirstQuality(bitrates, sizes, 0, 0, 0));
    }

    @Test
    public void qoeScoreBalancesQualityAndRebuffering() {
        MpcDecisionEngine engine = new MpcDecisionEngine(5, 4, 16, 1);
        double[] bitrates = {1, 4};
        long[][] sizes = {
                {500_000, 500_000, 500_000, 500_000, 500_000},
                {2_000_000, 2_000_000, 2_000_000, 2_000_000, 2_000_000}
        };
        assertEquals(1, engine.chooseFirstQuality(bitrates, sizes, 10, 30, 0));
        assertEquals(0, engine.chooseFirstQuality(bitrates, sizes, 1, 0, 0));
    }
}
