/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player.transport;

import java.net.URI;

public final class RedactedUrl {
    private RedactedUrl() {}

    public static String describe(URI uri) {
        if (uri == null) return "<null>";
        String scheme = uri.getScheme() == null ? "unknown" : uri.getScheme();
        String path = uri.getPath();
        String suffix = path == null || path.isEmpty()
                ? "/"
                : path.substring(path.lastIndexOf('/') + 1);
        if (suffix.isEmpty()) suffix = "/";
        return scheme + "://<redacted>/" + suffix;
    }
}
