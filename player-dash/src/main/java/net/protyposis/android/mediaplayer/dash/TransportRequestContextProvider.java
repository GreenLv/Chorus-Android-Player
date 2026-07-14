/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package net.protyposis.android.mediaplayer.dash;

import org.chorusresearch.player.transport.TransportRequest;

/** Adds player-side decision metadata before a media-segment request is sent. */
interface TransportRequestContextProvider {
    void addRequestContext(
            TransportRequest.Builder request,
            Representation representation,
            int segmentIndex);
}
