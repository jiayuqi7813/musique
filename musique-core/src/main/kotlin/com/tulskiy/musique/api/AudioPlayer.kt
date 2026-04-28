package com.tulskiy.musique.api

import com.tulskiy.musique.audio.player.Player
import com.tulskiy.musique.audio.player.PlayerEvent
import com.tulskiy.musique.audio.player.PlayerListener
import com.tulskiy.musique.playlist.PlaybackOrder
import com.tulskiy.musique.playlist.Playlist
import com.tulskiy.musique.system.TrackIO

/**
 * Main entry point for the musique audio player library.
 *
 * Provides a clean, GUI-free API for audio playback control.
 *
 * ## Basic Usage
 * ```kotlin
 * val player = AudioPlayer()
 * val item = MediaItem.fromFile(File("/path/to/music.mp3"))
 * player.open(item)
 * player.play()
 * // ...
 * player.stop()
 * player.close()
 * ```
 */
class AudioPlayer {

    private val engine = Player()
    private val playlist = Playlist()
    private val playbackOrder = PlaybackOrder()
    private val eventListeners = mutableListOf<AudioPlayerListener>()

    init {
        playbackOrder.setPlaylist(playlist)
        engine.setPlaybackOrder(playbackOrder)

        engine.addListener(PlayerListener { event ->
            val state = when (event.eventCode) {
                PlayerEvent.PlayerEventCode.PLAYING_STARTED -> PlaybackState.PLAYING
                PlayerEvent.PlayerEventCode.PAUSED -> PlaybackState.PAUSED
                PlayerEvent.PlayerEventCode.STOPPED -> PlaybackState.STOPPED
                PlayerEvent.PlayerEventCode.FILE_OPENED -> null
                PlayerEvent.PlayerEventCode.SEEK_FINISHED -> null
            }
            state?.let { notifyStateChanged(it) }
            notifyRawEvent(event.eventCode)
        })
    }

    // ─── Playback control ────────────────────────────────────────────────

    /**
     * Open and start playing a [MediaItem].
     * Replaces the current track.
     */
    fun open(item: MediaItem) {
        val track = item.toTrack()
        val location = track.trackData.location
        if (location != null && !track.trackData.isStream) {
            val file = track.trackData.getFile()
            val reader = TrackIO.getAudioFileReader(file.name)
            val populated = reader?.read(file)
            engine.open(populated ?: track)
        } else {
            engine.open(track)
        }
    }

    /** Begin or resume playback. */
    fun play() = engine.play()

    /** Pause playback (can be resumed with [play]). */
    fun pause() = engine.pause()

    /** Stop playback entirely. */
    fun stop() = engine.stop()

    /**
     * Seek to a position in the current track.
     * @param positionMs Position in milliseconds.
     */
    fun seek(positionMs: Long) {
        val track = engine.getTrack() ?: return
        val sampleRate = track.trackData.sampleRate
        if (sampleRate > 0) {
            val sample = (positionMs * sampleRate) / 1000L
            engine.seek(sample)
        }
    }

    /** Skip to the next track in the internal playlist (if any). */
    fun next() = engine.next()

    /** Skip to the previous track in the internal playlist (if any). */
    fun prev() = engine.prev()

    // ─── Queue / Playlist ────────────────────────────────────────────────

    /**
     * Add a [MediaItem] to the end of the internal playlist.
     */
    fun enqueue(item: MediaItem) {
        val track = item.toTrack()
        playlist.add(track)
    }

    /**
     * Clear all items from the internal playlist.
     */
    fun clearQueue() {
        playlist.clear()
    }

    /**
     * Set the playback order mode.
     */
    fun setPlaybackOrder(order: PlaybackOrder.Order) {
        playbackOrder.setOrder(order)
    }

    // ─── Volume ──────────────────────────────────────────────────────────

    /**
     * Get the current volume (0.0 to 1.0).
     */
    fun getVolume(): Float = engine.audioOutput.getVolume(false)

    /**
     * Set the volume level.
     * @param volume Value between 0.0 (mute) and 1.0 (full).
     */
    fun setVolume(volume: Float) {
        engine.audioOutput.setVolume(volume.coerceIn(0f, 1f))
    }

    // ─── State ───────────────────────────────────────────────────────────

    /**
     * The current [PlaybackState].
     */
    val state: PlaybackState
        get() = when {
            engine.isPlaying -> PlaybackState.PLAYING
            engine.isStopped -> PlaybackState.STOPPED
            else -> PlaybackState.PAUSED
        }

    /**
     * The currently playing [MediaItem], or null if stopped.
     */
    val currentItem: MediaItem?
        get() = engine.getTrack()?.let { MediaItem.fromTrack(it) }

    /**
     * Current playback position in milliseconds.
     */
    val positionMs: Long
        get() {
            val track = engine.getTrack() ?: return 0L
            val sampleRate = track.trackData.sampleRate
            return if (sampleRate > 0) (engine.currentSample * 1000L) / sampleRate else 0L
        }

    /**
     * Whether the player is currently playing.
     */
    val isPlaying: Boolean get() = engine.isPlaying

    /**
     * Whether the player is paused.
     */
    val isPaused: Boolean get() = engine.isPaused

    /**
     * Whether the player is stopped.
     */
    val isStopped: Boolean get() = engine.isStopped

    // ─── Events ──────────────────────────────────────────────────────────

    /**
     * Add a listener for playback state changes.
     */
    fun addListener(listener: AudioPlayerListener) {
        eventListeners.add(listener)
    }

    /**
     * Remove a previously added listener.
     */
    fun removeListener(listener: AudioPlayerListener) {
        eventListeners.remove(listener)
    }

    private fun notifyStateChanged(state: PlaybackState) {
        for (l in eventListeners) l.onStateChanged(state)
    }

    private fun notifyRawEvent(code: PlayerEvent.PlayerEventCode) {
        for (l in eventListeners) l.onEvent(code)
    }

    // ─── Lifecycle ───────────────────────────────────────────────────────

    /**
     * Release all resources held by the player.
     * Call this when done using the player.
     */
    fun close() {
        engine.stop()
    }

    /**
     * Retrieve supported audio format extensions.
     */
    fun supportedFormats(): Set<String> = com.tulskiy.musique.system.Codecs.getFormats()
}

/**
 * Listener interface for [AudioPlayer] events.
 */
interface AudioPlayerListener {
    /**
     * Called when the playback state changes.
     */
    fun onStateChanged(state: PlaybackState) {}

    /**
     * Called for raw player engine events.
     * For most use cases, prefer [onStateChanged].
     */
    fun onEvent(code: PlayerEvent.PlayerEventCode) {}
}
