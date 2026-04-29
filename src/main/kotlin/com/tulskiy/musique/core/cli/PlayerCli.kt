package com.tulskiy.musique.core.cli

import com.tulskiy.musique.core.player.JavaSoundAudioPlayer
import java.net.URI
import java.nio.file.Paths

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Usage: ./gradlew run --args='path/to/file.wav' OR --args='https://host/audio.wav'")
        return
    }

    val source = args[0]
    JavaSoundAudioPlayer().use { player ->
        if (source.startsWith("http://") || source.startsWith("https://")) {
            player.load(URI.create(source))
        } else {
            player.load(Paths.get(source))
        }
        player.play()
        println("Playing: $source")
        println("Duration(ms): ${player.durationMillis ?: -1}")
        println("Press ENTER to stop...")
        readlnOrNull()
        player.stop()
    }
}
