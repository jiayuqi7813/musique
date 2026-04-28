package com.tulskiy.musique.api

import com.tulskiy.musique.playlist.Track
import com.tulskiy.musique.playlist.TrackData
import java.io.File
import java.net.URI

/**
 * Represents a playable media item (track/file).
 *
 * This is a high-level wrapper around [Track] providing a clean API
 * for embedding applications.
 */
data class MediaItem(
    /** The URI of the media resource (file or stream). */
    val uri: URI,
    /** Display title. Falls back to filename if not set. */
    val title: String? = null,
    /** Artist name. */
    val artist: String? = null,
    /** Album name. */
    val album: String? = null,
    /** Track duration in milliseconds. -1 if unknown. */
    val durationMs: Long = -1L,
    /** CUE sub-song index (0 for normal files). */
    val subsongIndex: Int = 0
) {
    companion object {
        /** Create a MediaItem from a File. */
        @JvmStatic
        fun fromFile(file: File): MediaItem = MediaItem(uri = file.toURI())

        /** Create a MediaItem from a URI string. */
        @JvmStatic
        fun fromUri(uriString: String): MediaItem = MediaItem(uri = URI.create(uriString))

        /** Create a MediaItem from an existing [Track] (internal use). */
        @JvmStatic
        fun fromTrack(track: Track): MediaItem {
            val data = track.trackData
            val sampleRate = data.sampleRate
            val totalSamples = data.totalSamples
            val durationMs = if (sampleRate > 0 && totalSamples > 0)
                (totalSamples * 1000L) / sampleRate
            else -1L
            return MediaItem(
                uri = data.location ?: URI.create(""),
                title = data.title,
                artist = data.artist,
                album = data.album,
                durationMs = durationMs,
                subsongIndex = data.subsongIndex
            )
        }
    }

    /** Convert to an internal [Track] object for the playback engine. */
    fun toTrack(): Track {
        val data = TrackData(uri, subsongIndex)
        title?.let { data.addTitle(it) }
        artist?.let { data.addArtist(it) }
        album?.let { data.addAlbum(it) }
        return Track(data)
    }
}
