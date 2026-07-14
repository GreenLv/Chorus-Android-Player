/*
 * Copyright 2026 Gerui Lv and Qingyue Tan
 * Licensed under the Apache License, Version 2.0.
 */
package org.chorusresearch.player;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

final class MpdUrlValidator {
    private MpdUrlValidator() {}

    static URI requireHttpsMpd(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("MPD URL is required");
        }
        try {
            URI uri = new URI(value.trim());
            if (!"https".equalsIgnoreCase(uri.getScheme())) {
                throw new IllegalArgumentException("Only HTTPS MPD URLs are accepted");
            }
            if (uri.getHost() == null || uri.getHost().isEmpty()) {
                throw new IllegalArgumentException("MPD URL must include a host");
            }
            String path = uri.getPath();
            if (path == null || !path.toLowerCase(Locale.ROOT).endsWith(".mpd")) {
                throw new IllegalArgumentException("URL path must end with .mpd");
            }
            return uri;
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("MPD URL is invalid");
        }
    }
}
