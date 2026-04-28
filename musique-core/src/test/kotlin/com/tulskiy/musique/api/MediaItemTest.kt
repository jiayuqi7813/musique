package com.tulskiy.musique.api

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URI

class MediaItemTest {

    @Test
    fun `fromFile creates MediaItem with correct URI`() {
        val file = File("/tmp/test.mp3")
        val item = MediaItem.fromFile(file)
        assertEquals(file.toURI(), item.uri)
    }

    @Test
    fun `fromUri creates MediaItem with correct URI`() {
        val uriStr = "http://example.com/stream"
        val item = MediaItem.fromUri(uriStr)
        assertEquals(URI.create(uriStr), item.uri)
    }

    @Test
    fun `MediaItem with metadata stores fields correctly`() {
        val item = MediaItem(
            uri = URI.create("file:///tmp/song.flac"),
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            durationMs = 180_000L
        )
        assertEquals("Test Song", item.title)
        assertEquals("Test Artist", item.artist)
        assertEquals("Test Album", item.album)
        assertEquals(180_000L, item.durationMs)
    }

    @Test
    fun `toTrack round-trip preserves location`() {
        val uri = URI.create("file:///tmp/song.mp3")
        val item = MediaItem(uri = uri, title = "Hello", artist = "World", album = "Album")
        val track = item.toTrack()
        assertEquals(uri, track.trackData.location)
    }

    @Test
    fun `AudioPlayer initial state is STOPPED`() {
        val player = AudioPlayer()
        assertEquals(PlaybackState.STOPPED, player.state)
        assertTrue(player.isStopped)
        assertFalse(player.isPlaying)
        assertFalse(player.isPaused)
        player.close()
    }

    @Test
    fun `AudioPlayer supportedFormats includes common formats`() {
        val player = AudioPlayer()
        val formats = player.supportedFormats()
        assertTrue(formats.contains("mp3"), "should support mp3")
        assertTrue(formats.contains("flac"), "should support flac")
        assertTrue(formats.contains("ogg"), "should support ogg")
        player.close()
    }

    @Test
    fun `PlaybackState enum has three states`() {
        val states = PlaybackState.values()
        assertEquals(3, states.size)
        assertTrue(states.contains(PlaybackState.PLAYING))
        assertTrue(states.contains(PlaybackState.PAUSED))
        assertTrue(states.contains(PlaybackState.STOPPED))
    }
}
