package com.jarvis.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jarvis.android.R
import timber.log.Timber

/**
 * 🎙️ Foreground service for always-on Wake Word + STT.
 * Uses Android SpeechRecognizer (free, on-device) + VAD.
 */
class JarvisVoiceService : Service() {

    companion object {
        const val NOTIF_ID = 1001
        const val CHANNEL_ID = "jarvis_voice"
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        Timber.i("🎙️ JarvisVoiceService created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification())
        when (intent?.action) {
            "START_LISTENING" -> startListening()
            "STOP_LISTENING" -> stopListening()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "JARVIS Voice", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Always listening for Hey Jarvis"
                setShowBadge(false)
            }
            (getSystemService(NotificationManager::class.java)).createNotificationChannel(ch)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🎙️ JARVIS گوش به زنگه")
            .setContentText("بگو: «هی جارویس»")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun startListening() {
        Timber.i("🎙️ Starting listening — Wake Word: Hey Jarvis")
        // In production: init openWakeWord / SpeechRecognizer
        // For now: placeholder that can be extended with ML Kit / Vosk / Whisper
    }

    private fun stopListening() {
        Timber.i("🎙️ Stopped listening")
    }
}
