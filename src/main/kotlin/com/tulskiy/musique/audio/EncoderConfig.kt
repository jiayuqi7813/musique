package com.tulskiy.musique.audio

import java.util.concurrent.ConcurrentHashMap

/**
 * Mutable encoder settings map implementing [EncoderOptions] for encoders without XML config.
 */
class EncoderConfig : EncoderOptions {
    private val floats = ConcurrentHashMap<String, Float>()
    private val ints = ConcurrentHashMap<String, Int>()
    private val booleans = ConcurrentHashMap<String, Boolean>()
    private val strings = ConcurrentHashMap<String, String>()

    fun putFloat(key: String, value: Float) {
        floats[key] = value
    }

    fun putInt(key: String, value: Int) {
        ints[key] = value
    }

    fun putBoolean(key: String, value: Boolean) {
        booleans[key] = value
    }

    fun putString(key: String, value: String) {
        strings[key] = value
    }

    override fun getFloat(key: String, defaultValue: Float): Float = floats[key] ?: defaultValue

    override fun getInt(key: String, defaultValue: Int): Int = ints[key] ?: defaultValue

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = booleans[key] ?: defaultValue

    override fun getString(key: String, defaultValue: String?): String? = strings[key] ?: defaultValue
}
