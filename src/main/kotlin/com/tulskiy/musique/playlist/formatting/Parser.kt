package com.tulskiy.musique.playlist.formatting

import com.tulskiy.musique.playlist.Track
import com.tulskiy.musique.playlist.formatting.tokens.Expression
import com.tulskiy.musique.util.Util

/**
 * Minimal format evaluator for playback order (optional `[%x%]`, `%field%` tokens).
 * Replaces the original Parser + Methods stack for headless builds.
 */
object Parser {

    @JvmStatic
    fun parse(text: String): Expression {
        val normalized = text.replace("\\s*,\\s*".toRegex(), ",")
        return Expression { track -> evalFormat(normalized, track) }
    }

    private fun evalFormat(input: String, track: Track): String {
        val i = input.indexOf('[')
        if (i >= 0) {
            val j = input.indexOf(']', i)
            if (j > i) {
                val inner = evalFormat(input.substring(i + 1, j), track)
                val prefix = if (!Util.isEmpty(inner)) inner else ""
                return input.substring(0, i) + prefix + evalFormat(input.substring(j + 1), track)
            }
        }
        return replaceFields(input, track)
    }

    private fun replaceFields(s: String, track: Track): String {
        val td = track.trackData
        val map = mapOf(
            "artist" to td.getArtist(),
            "title" to td.getTitle(),
            "album" to td.getAlbum(),
            "album artist" to td.getAlbumArtist(),
            "album_artist" to td.getAlbumArtist(),
        )
        return TOKEN.replace(s) { m ->
            val key = m.groupValues[1].lowercase().replace('_', ' ')
            map[key] ?: m.value
        }
    }

    private val TOKEN = Regex("%([^%]+)%")
}
