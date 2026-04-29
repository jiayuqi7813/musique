package com.tulskiy.musique.audio;

/**
 * Encoder settings without tying decoders to XML UI configuration.
 */
public interface EncoderOptions {

    float getFloat(String key, float defaultValue);

    int getInt(String key, int defaultValue);

    boolean getBoolean(String key, boolean defaultValue);

    /** @param defaultValue may be null (e.g. optional encoder mode string). */
    String getString(String key, String defaultValue);
}
