package com.jarvis.android.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class TTSManager(context: Context) {
    private var tts: TextToSpeech? = TextToSpeech(context) { status ->
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("fa", "IR")
        }
    }

    fun speak(text: String, lang: String = "fa") {
        val locale = if (lang == "fa") Locale("fa", "IR") else Locale.ENGLISH
        tts?.language = locale
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis-tts")
    }

    fun stop() { tts?.stop() }
    fun shutdown() { tts?.shutdown() }
}
