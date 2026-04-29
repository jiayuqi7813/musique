package com.tulskiy.musique.core.player

import com.tulskiy.musique.core.api.PlayerState
import kotlin.test.Test
import kotlin.test.assertEquals

class JavaSoundAudioPlayerTest {
    @Test
    fun initialStateIsIdle() {
        JavaSoundAudioPlayer().use { player ->
            assertEquals(PlayerState.IDLE, player.state)
            assertEquals(0L, player.positionMillis)
        }
    }
}
