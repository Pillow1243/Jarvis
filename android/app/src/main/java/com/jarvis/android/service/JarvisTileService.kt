package com.jarvis.android.service

import android.content.Intent
import android.service.quicksettings.TileService
import com.jarvis.android.MainActivity

class JarvisTileService : TileService() {
    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) }
        startActivityAndCollapse(intent)
    }
}
