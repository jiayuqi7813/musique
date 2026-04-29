package com.tulskiy.musique.audio.api

/**
 * Headless entry: `gradle run` or `java -jar ...` with optional audio file path as first argument.
 * Without arguments, exits successfully after loading native player threads (no file opened).
 */
fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("musique-audio: pass a path to an audio file to decode and play briefly (requires audio output).")
        return
    }
    val api = PlayerApi()
    val track = api.addFile(args[0])
    api.open(track)
    api.play()
    Thread.sleep(1500)
    api.stop()
}
