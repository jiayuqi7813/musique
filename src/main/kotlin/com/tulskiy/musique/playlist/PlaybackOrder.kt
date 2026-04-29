package com.tulskiy.musique.playlist

import com.tulskiy.musique.playlist.formatting.Parser
import com.tulskiy.musique.playlist.formatting.tokens.Expression

/**
 * Playback queue and next/prev logic (Kotlin port; no Application/XML configuration).
 * Album grouping format defaults to `%album%`; call [setAlbumFormatProperty] to change at runtime.
 */
class PlaybackOrder {

    enum class Order(private val text: String) {
        DEFAULT("Default"),
        REPEAT("Repeat"),
        REPEAT_TRACK("Repeat track"),
        REPEAT_ALBUM("Repeat album"),
        REPEAT_GROUP("Repeat group"),
        SHUFFLE("Shuffle"),
        SHUFFLE_ALBUMS("Shuffle albums"),
        SHUFFLE_GROUPS("Shuffle groups"),
        RANDOM("Random");

        override fun toString(): String = text
    }

    private val queueTupleTitle: Expression = Parser.parse("[%artist% - ]%title%")

    inner class QueueTuple(var track: Track, var playlist: Playlist) {
        override fun toString(): String = queueTupleTitle.eval(track) as String
    }

    private var backingPlaylist: Playlist? = null

    fun getPlaylist(): Playlist? = backingPlaylist

    fun setPlaylist(playlist: Playlist?) {
        this.backingPlaylist = playlist
    }

    var order: Order = Order.DEFAULT
    private val queue: MutableList<QueueTuple> = ArrayList()
    var lastPlayed: Track? = null
    private var albumFormat: Expression = Parser.parse("%album%")

    fun setAlbumFormatProperty(format: String) {
        albumFormat = Parser.parse(format)
    }

    fun getQueue(): List<QueueTuple> = queue

    fun enqueue(track: Track, playlist: Playlist) {
        queue.add(QueueTuple(track, playlist))
        updateQueuePositions()
    }

    fun updateQueuePositions() {
        for (i in queue.indices) {
            queue[i].track.setQueuePosition(i + 1)
        }
    }

    fun flushQueue() {
        for (tuple in queue) {
            tuple.track.setQueuePosition(-1)
        }
        queue.clear()
        updateQueuePositions()
    }

    private fun getTrack(index: Int): Track? {
        if (index != -1) {
            val pl = backingPlaylist ?: return null
            val track = pl[index]
            return if (track.trackData.location == null) pl[index + 1] else track
        }
        return null
    }

    private fun next(index: Int): Track? {
        val pl = backingPlaylist ?: return null
        var idx = if (index < pl.size - 1) index + 1 else -1
        if (idx != -1) {
            var track = pl[idx]
            if (track.trackData.location == null) {
                return next(idx)
            }
            return track
        }
        return null
    }

    private fun prev(index: Int): Track? {
        val pl = backingPlaylist ?: return null
        var idx = index - 1
        if (idx >= 0) {
            var track = pl[idx]
            if (track.trackData.location == null) {
                return prev(idx)
            }
            return track
        }
        return null
    }

    fun next(currentTrack: Track?): Track? {
        if (queue.isNotEmpty()) {
            val tuple = queue.removeAt(0)
            val track = tuple.track
            setPlaylist(tuple.playlist)
            track.setQueuePosition(-1)
            updateQueuePositions()
            return track
        }

        val pl = backingPlaylist
        if (pl == null || pl.isEmpty()) {
            return null
        }

        lastPlayed?.let { lp ->
            if (pl.contains(lp)) {
                lastPlayed = null
                return lp
            }
        }

        if (currentTrack == null) {
            return pl[0]
        }

        var index = pl.indexOf(currentTrack)
        if (index == -1) {
            return pl[0]
        }

        var track: Track?
        when (order) {
            Order.DEFAULT -> return next(index)
            Order.REPEAT -> {
                track = next(index)
                return track ?: getTrack(0)
            }
            Order.REPEAT_TRACK -> return currentTrack
            Order.REPEAT_ALBUM -> return nextPatternMatch(currentTrack, index, albumFormat, false)
            Order.REPEAT_GROUP -> {
                if (index + 1 < pl.size) {
                    val tr = pl[index + 1]
                    if (tr.trackData.location != null) {
                        return tr
                    }
                }
                for (i in index downTo 0) {
                    if (pl[i].trackData.location == null) {
                        return pl[i + 1]
                    }
                }
                return pl[0]
            }
            Order.SHUFFLE_ALBUMS -> return nextPatternMatch(currentTrack, index, albumFormat, true)
            Order.SHUFFLE_GROUPS -> {
                if (index + 1 < pl.size) {
                    val tr = pl[index + 1]
                    if (tr.trackData.location != null) {
                        return tr
                    }
                }
                for (i in index downTo 0) {
                    val separator = pl[i]
                    if (separator.trackData.location == null) {
                        val shuffled = nextShuffle(separator, true, null)
                        return next(pl.indexOf(shuffled))
                    }
                }
                return pl[0]
            }
            Order.RANDOM -> return nextRandom()
            Order.SHUFFLE -> return nextShuffle(currentTrack, false, null)
        }
    }

    private fun nextPatternMatch(currentTrack: Track, index: Int, pattern: Expression, shuffle: Boolean): Track? {
        val pl = backingPlaylist ?: return null
        val result = pattern.eval(currentTrack)
        var track = next(index)
        if (track != null) {
            if (equalsObj(result, pattern.eval(track))) {
                return track
            }
        }
        for (i in index downTo 0) {
            track = pl[i]
            if (!equalsObj(result, pattern.eval(track))) {
                val nxt = next(i)
                return if (shuffle) nxt?.let { nextShuffle(it, false, pattern) } else nxt
            }
        }
        return track
    }

    fun prev(currentTrack: Track?): Track? {
        val pl = backingPlaylist
        if (pl == null || pl.isEmpty()) {
            return null
        }
        val index = pl.indexOf(currentTrack ?: return null)
        if (index == -1) {
            return null
        }
        val size = pl.size
        var track: Track?
        when (order) {
            Order.DEFAULT -> return prev(index)
            Order.REPEAT -> {
                track = prev(index)
                return track ?: getTrack(size - 1)
            }
            Order.REPEAT_TRACK -> return currentTrack
            Order.REPEAT_ALBUM -> return prevPatternMatch(currentTrack, index, albumFormat, false)
            Order.REPEAT_GROUP -> {
                if (index > 0) {
                    val tr = pl[index - 1]
                    if (tr.trackData.location != null) {
                        return tr
                    }
                }
                for (i in index + 1 until size) {
                    if (pl[i].trackData.location == null) {
                        return pl[i - 1]
                    }
                }
                return pl[size - 1]
            }
            Order.SHUFFLE_ALBUMS -> return prevPatternMatch(currentTrack, index, albumFormat, true)
            Order.SHUFFLE_GROUPS -> {
                if (index > 0) {
                    val tr = pl[index - 1]
                    if (tr.trackData.location != null) {
                        return tr
                    }
                }
                for (i in index downTo 0) {
                    val separator = pl[i]
                    if (separator.trackData.location == null) {
                        val shuffled = prevShuffle(separator, true, null)
                        return next(pl.indexOf(shuffled))
                    }
                }
                return pl[0]
            }
            Order.RANDOM -> return nextRandom()
            Order.SHUFFLE -> return prevShuffle(currentTrack, false, null)
        }
    }

    fun nextRandom(): Track? {
        val pl = backingPlaylist ?: return null
        return getTrack((Math.random() * pl.size).toInt())
    }

    private fun equalsObj(o1: Any?, o2: Any?): Boolean =
        (o1 != null && o1 == o2) || (o1 == null && o2 == null)

    private fun prevPatternMatch(currentTrack: Track, index: Int, pattern: Expression, shuffle: Boolean): Track? {
        val pl = backingPlaylist ?: return null
        val result = pattern.eval(currentTrack)
        var track = prev(index)
        if (track != null) {
            if (equalsObj(result, pattern.eval(track))) {
                return track
            }
        }
        if (shuffle) {
            return prevShuffle(currentTrack, false, pattern)
        }
        for (i in index until pl.size) {
            track = pl[i]
            if (!equalsObj(result, pattern.eval(track))) {
                return prev(i)
            }
        }
        return track
    }

    private fun nextShuffle(currentTrack: Track?, searchSeparators: Boolean, pattern: Expression?): Track? {
        val pl = backingPlaylist ?: return null
        if (currentTrack == null) return null
        var minRating: Track? = null
        var minGreater: Track? = null
        var patternValue: Any? = null
        for (track in pl) {
            if (track === currentTrack ||
                (searchSeparators && track.trackData.location != null) ||
                (!searchSeparators && track.trackData.location == null)
            ) {
                continue
            }
            if (pattern != null) {
                val value = pattern.eval(track)
                if (equalsObj(patternValue, value)) {
                    continue
                }
                patternValue = value
            }
            if (minRating == null || track.shuffleRating < minRating.shuffleRating) {
                minRating = track
            }
            if (track.shuffleRating >= currentTrack.shuffleRating) {
                if (minGreater == null || track.shuffleRating < minGreater.shuffleRating) {
                    minGreater = track
                }
            }
        }
        return minGreater ?: minRating
    }

    private fun prevShuffle(currentTrack: Track?, searchSeparators: Boolean, pattern: Expression?): Track? {
        val pl = backingPlaylist ?: return null
        if (currentTrack == null) return null
        var maxSmaller: Track? = null
        var maxRating: Track? = null
        var patternValue: Any? = null
        for (track in pl) {
            if (track === currentTrack ||
                (searchSeparators && track.trackData.location != null) ||
                (!searchSeparators && track.trackData.location == null)
            ) {
                continue
            }
            if (pattern != null) {
                val value = pattern.eval(track)
                if (equalsObj(patternValue, value)) {
                    continue
                }
                patternValue = value
            }
            if (maxRating == null || track.shuffleRating > maxRating.shuffleRating) {
                maxRating = track
            }
            if (track.shuffleRating <= currentTrack.shuffleRating) {
                if (maxSmaller == null || track.shuffleRating > maxSmaller.shuffleRating) {
                    maxSmaller = track
                }
            }
        }
        return maxSmaller ?: maxRating
    }

    fun trackPlayable(track: Track): Boolean = backingPlaylist?.contains(track) == true
}
