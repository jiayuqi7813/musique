package com.tulskiy.musique.playlist.formatting.tokens

import com.tulskiy.musique.playlist.Track

fun interface Expression {
    fun eval(track: Track): Any?
}
