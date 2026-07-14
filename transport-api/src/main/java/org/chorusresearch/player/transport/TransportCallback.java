/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

import java.io.IOException;

public interface TransportCallback {
    void onResponse(TransportResponse response);
    void onFailure(IOException exception);
}
