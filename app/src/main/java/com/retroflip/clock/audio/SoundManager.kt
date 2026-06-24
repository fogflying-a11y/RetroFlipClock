package com.retroflip.clock.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

class SoundManager(private val context: Context) {

    companion object {
        private const val TAG = "SoundManager"
        private const val MAX_STREAMS = 4
    }

    private var soundPool: SoundPool? = null
    private var soundIds = HashMap<String, Int>()
    private var currentLoopStreamId = -1
    private var currentTheme: String? = null

    private val themes = mapOf(
        "slow_cinematic_clock_ticking" to "slow_cinematic_clock_ticking.wav",
        "clock_ticking_sfx" to "clock_ticking_sfx.wav",
        "clock_ticking_down" to "clock_ticking_down.wav"
    )

    private val loopThemes = setOf("slow_cinematic_clock_ticking", "clock_ticking_sfx")

    init {
        initSoundPool()
        preloadAllSounds()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(MAX_STREAMS)
            .setAudioAttributes(audioAttributes)
            .build()
    }

    private fun preloadAllSounds() {
        val pool = soundPool ?: return
        themes.forEach { (key, fileName) ->
            try {
                context.assets.openFd("sounds/$fileName").use { fd ->
                    val id = pool.load(fd.fileDescriptor, fd.startOffset, fd.length, 1)
                    soundIds[key] = id
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to preload sound: $fileName", e)
            }
        }
    }

    fun playLoop(theme: String) {
        if (currentTheme == theme && currentLoopStreamId != -1) return

        stopLoop()

        if (theme !in loopThemes) return

        currentTheme = theme
        val pool = soundPool ?: return
        val soundId = soundIds[theme] ?: return

        try {
            currentLoopStreamId = pool.play(soundId, 1f, 1f, 1, -1, 1f)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start loop for: $theme", e)
        }
    }

    fun playFlip(theme: String) {
        if (theme in loopThemes) return

        val pool = soundPool ?: return
        val soundId = soundIds[theme] ?: return

        try {
            pool.play(soundId, 1f, 1f, 1, 0, 1f)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play flip sound: $theme", e)
        }
    }

    fun stopLoop() {
        val pool = soundPool ?: return
        if (currentLoopStreamId != -1) {
            pool.stop(currentLoopStreamId)
            currentLoopStreamId = -1
        }
        currentTheme = null
    }

    fun stopAll() {
        stopLoop()
    }

    fun resumeLoop(theme: String, enabled: Boolean) {
        if (!enabled) {
            stopLoop()
            return
        }
        if (theme in loopThemes) {
            playLoop(theme)
        }
    }

    fun release() {
        stopAll()
        soundPool?.release()
        soundPool = null
    }
}
