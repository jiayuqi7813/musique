package com.tulskiy.musique.api

/**
 * Represents the current playback state of the audio player.
 */
enum class PlaybackState {
    /** Player is actively playing audio. */
    PLAYING,
    /** Player is paused (can be resumed). */
    PAUSED,
    /** Player is stopped (no active track). */
    STOPPED
}
