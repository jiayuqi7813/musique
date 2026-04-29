package com.tulskiy.musique.core.api

import java.net.URI
import java.nio.file.Path

interface AudioPlayer : AutoCloseable {
    val state: PlayerState
    val positionMillis: Long
    val durationMillis: Long?

    fun load(path: Path)
    fun load(uri: URI)
    fun play()
    fun pause()
    fun stop()
    override fun close()
}
