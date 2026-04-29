package com.tulskiy.musique.core.player

import com.tulskiy.musique.core.api.AudioPlayer
import com.tulskiy.musique.core.api.PlayerState
import java.net.URI
import java.nio.file.Path
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.LineEvent

class JavaSoundAudioPlayer : AudioPlayer {
    private var clip: Clip? = null
    override var state: PlayerState = PlayerState.IDLE
        private set

    override val positionMillis: Long
        get() = clip?.microsecondPosition?.div(1_000) ?: 0L

    override val durationMillis: Long?
        get() = clip?.microsecondLength?.div(1_000)

    override fun load(path: Path) {
        loadInternal(AudioSystem.getAudioInputStream(path.toFile()))
    }

    override fun load(uri: URI) {
        val source = uri.toURL()
        loadInternal(AudioSystem.getAudioInputStream(source))
    }

    private fun loadInternal(stream: AudioInputStream) {
        stop()
        closeClip()
        stream.use { input ->
            val loadedClip = AudioSystem.getClip()
            loadedClip.open(input)
            loadedClip.addLineListener { event ->
                if (event.type == LineEvent.Type.STOP && state == PlayerState.PLAYING) {
                    if (loadedClip.microsecondPosition >= loadedClip.microsecondLength) {
                        state = PlayerState.STOPPED
                    }
                }
            }
            clip = loadedClip
        }
        state = PlayerState.LOADED
    }

    override fun play() {
        val loaded = clip ?: error("No audio loaded")
        loaded.start()
        state = PlayerState.PLAYING
    }

    override fun pause() {
        val loaded = clip ?: error("No audio loaded")
        loaded.stop()
        state = PlayerState.PAUSED
    }

    override fun stop() {
        clip?.apply {
            stop()
            microsecondPosition = 0
        }
        if (clip != null) state = PlayerState.STOPPED
    }

    override fun close() {
        closeClip()
        state = PlayerState.IDLE
    }

    private fun closeClip() {
        clip?.close()
        clip = null
    }
}
