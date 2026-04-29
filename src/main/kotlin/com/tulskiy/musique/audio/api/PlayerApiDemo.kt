package com.tulskiy.musique.audio.api

/**
 * Headless entry: `gradle run` or `java -jar ...` with optional audio file path as first argument.
 * Without arguments, exits successfully after loading native player threads (no file opened).
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println(
            "musique-audio: pass a local path or http(s) URL to play briefly (requires audio output). " +
                "Example: ./gradlew run --args=\"https://example.com/track.mp3\""
        )
        return
    }
    val api = PlayerApi()
    val arg = args[0].trim()
    val track = if (arg.startsWith("http://", ignoreCase = true) || arg.startsWith("https://", ignoreCase = true)) {
        api.addHttpUrl(arg)
    } else {
        api.addFile(arg)
    }
    api.open(track)
    api.play()
    Thread.sleep(1500)
    api.stop()
}
