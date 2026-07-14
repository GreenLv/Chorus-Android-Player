/* Copyright 2026 Gerui Lv and Qingyue Tan. Licensed under Apache-2.0. */
package net.protyposis.android.mediaplayer.dash;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalLong;
import org.chorusresearch.player.transport.Cancellable;
import org.chorusresearch.player.transport.ServerPathStats;
import org.chorusresearch.player.transport.TransportCallback;
import org.chorusresearch.player.transport.TransportCapabilities;
import org.chorusresearch.player.transport.TransportClient;
import org.chorusresearch.player.transport.TransportRequest;
import org.chorusresearch.player.transport.TransportResponse;
import org.junit.Test;

public class ChorusAdaptationLogicTest {
    @Test
    public void appliesPublishedMultipathPredictionFormula() {
        ServerPathStats stats = new ServerPathStats(1, 0, 1,
                8, 2, 20, 50, 0.8, 1000);
        assertEquals(10.0, ChorusAdaptationLogic.predictMultipathMbps(stats), 0.0001);
    }

    @Test
    public void fallsBackToFiveSampleHarmonicMeanWhenStatsAreStale() {
        FakeClient client = new FakeClient(new ServerPathStats(1, 0, 1,
                8, 2, 20, 50, 0.8, 0));
        ChorusAdaptationLogic logic = new ChorusAdaptationLogic(
                client, (representation, index) -> OptionalLong.empty(), () -> 5000, 1000);
        for (int i = 1; i <= 6; i++) {
            logic.reportSegmentDownload(null, null, null, i, i * 1_000_000, 1000);
        }
        double expected = 5.0 / (1.0 / 2 + 1.0 / 3 + 1.0 / 4 + 1.0 / 5 + 1.0 / 6);
        assertEquals(expected * 8, logic.currentPredictionMbps(), 0.0001);
    }

    private static final class FakeClient implements TransportClient {
        private final ServerPathStats stats;
        FakeClient(ServerPathStats stats) { this.stats = stats; }
        @Override public TransportCapabilities capabilities() {
            return new TransportCapabilities(1, true, true, true, true);
        }
        @Override public TransportResponse execute(TransportRequest request) throws IOException {
            throw new IOException("unused");
        }
        @Override public Cancellable enqueue(TransportRequest request, TransportCallback callback) {
            throw new UnsupportedOperationException();
        }
        @Override public Optional<ServerPathStats> latestServerPathStats() {
            return Optional.of(stats);
        }
        @Override public void close() {}
    }
}
