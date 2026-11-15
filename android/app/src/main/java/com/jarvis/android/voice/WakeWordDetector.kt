package com.jarvis.android.voice

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Wake Word detector — stub that can be backed by openWakeWord / Porcupine / Vosk.
 * For demo, triggers on transcript containing "هی جارویس" / "hey jarvis".
 */
class WakeWordDetector {
    private val _detected = MutableStateFlow(false)
    val detected: StateFlow<Boolean> = _detected

    private val triggers = listOf("هی جارویس", "یا جارویس", "hey jarvis", "hey jarvis,", "جارویس")

    fun check(transcript: String): Boolean {
        val hit = triggers.any { transcript.lowercase().contains(it) }
        if (hit) _detected.value = true
        return hit
    }

    fun reset() { _detected.value = false }
}
