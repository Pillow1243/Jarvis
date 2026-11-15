package com.jarvis.android.service

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.widget.TextView
import timber.log.Timber

/**
 * 🖼️ Floating HUD — draggable JARVIS orb overlay.
 * Requires SYSTEM_ALERT_WINDOW permission.
 */
class JarvisOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    override fun onCreate() {
        super.onCreate()
        Timber.i("🖼️ Overlay service created")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "SHOW") showOverlay()
        if (intent?.action == "HIDE") hideOverlay()
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun showOverlay() {
        if (overlayView != null) return
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            val tv = TextView(this).apply {
                text = "🤖"
                textSize = 28f
                setPadding(24, 24, 24, 24)
                setBackgroundColor(0xCC00D4FF.toInt())
            }
            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            ).apply { gravity = Gravity.TOP or Gravity.START; x = 100; y = 300 }

            var startX = 0; var startY = 0; var touchX = 0f; var touchY = 0f
            tv.setOnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> { startX = params.x; startY = params.y; touchX = event.rawX; touchY = event.rawY; true }
                    MotionEvent.ACTION_MOVE -> { params.x = startX + (event.rawX - touchX).toInt(); params.y = startY + (event.rawY - touchY).toInt(); windowManager?.updateViewLayout(tv, params); true }
                    MotionEvent.ACTION_UP -> {
                        if (kotlin.math.abs(event.rawX - touchX) < 10 && kotlin.math.abs(event.rawY - touchY) < 10) {
                            // Tap — open JARVIS
                            val i = Intent(this, com.jarvis.android.MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
                            startActivity(i)
                        }
                        true
                    }
                    else -> false
                }
            }
            windowManager?.addView(tv, params)
            overlayView = tv
        } catch (e: Exception) {
            Timber.e(e, "Overlay failed — need SYSTEM_ALERT_WINDOW")
        }
    }

    private fun hideOverlay() {
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
    }
}
