package com.tulskiy.musique.net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Small helpers for HTTP(S) media (content-type probe without full download).
 */
public final class NetworkMedia {
    private static final Logger logger = Logger.getLogger(NetworkMedia.class.getName());
    private static final int CONNECT_MS = 15000;
    private static final int READ_MS = 15000;

    private NetworkMedia() {
    }

    /**
     * Returns the {@code Content-Type} header value (without parameters), or null.
     */
    public static String probeContentType(URI uri) {
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        if (scheme == null || !scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
            return null;
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("HEAD");
            conn.setInstanceFollowRedirects(true);
            conn.setConnectTimeout(CONNECT_MS);
            conn.setReadTimeout(READ_MS);
            conn.setRequestProperty("Icy-Metadata", "1");
            conn.connect();
            String raw = conn.getContentType();
            if (raw == null || raw.isEmpty()) {
                return null;
            }
            int semi = raw.indexOf(';');
            return semi > 0 ? raw.substring(0, semi).trim() : raw.trim();
        } catch (IOException | ClassCastException e) {
            logger.log(Level.FINE, "HEAD probe failed for " + uri, e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
