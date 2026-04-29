package com.tulskiy.musique.audio.api

import com.tulskiy.musique.audio.player.Player
import com.tulskiy.musique.playlist.PlaybackOrder
import com.tulskiy.musique.playlist.Playlist
import com.tulskiy.musique.playlist.Track
import com.tulskiy.musique.playlist.TrackData
import java.io.File
import java.net.URI

/**
 * Thin Kotlin façade over [Player] + [PlaybackOrder] for programmatic use without the desktop app.
 */
class PlayerApi {
    val player: Player = Player()
    val playbackOrder: PlaybackOrder = PlaybackOrder()
    val playlist: Playlist = Playlist()

    init {
        player.setPlaybackOrder(playbackOrder)
        playbackOrder.setPlaylist(playlist)
    }

    fun addFile(path: String): Track {
        val file = File(path)
        val uri = file.toURI()
        val track = Track(TrackData(uri, 0))
        playlist.add(track)
        return track
    }

    /**
     * Adds a track from an http(s) URL (progressive file or ICY stream).
     * Scheme must be `http` or `https`.
     */
    fun addHttpUrl(url: String): Track {
        val uri = URI.create(url.trim())
        require(uri.scheme.equals("http", ignoreCase = true) || uri.scheme.equals("https", ignoreCase = true)) {
            "Only http and https URLs are supported: $url"
        }
        return addUri(uri)
    }

    fun addUri(uri: URI): Track {
        val track = Track(TrackData(uri, 0))
        playlist.add(track)
        return track
    }

    fun open(track: Track) {
        player.open(track)
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.stop()
    }

    fun seekSample(sample: Long) {
        player.seek(sample)
    }

    fun next() {
        player.next()
    }

    fun prev() {
        player.prev()
    }
}
