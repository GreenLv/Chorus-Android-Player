/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package net.protyposis.android.mediaplayer.dash;

import java.util.OptionalLong;

/** Supplies pre-request segment sizes when a standard MPD does not contain them. */
public interface SegmentSizeProvider {
    OptionalLong sizeBytes(Representation representation, int zeroBasedSegmentIndex);

    SegmentSizeProvider NONE = (representation, segmentIndex) -> OptionalLong.empty();
}
