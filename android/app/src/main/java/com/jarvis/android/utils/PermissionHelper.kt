package com.jarvis.android.utils

import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils

object PermissionHelper {
    fun isAccessibilityEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES) ?: return false
        val colon = TextUtils.SimpleStringSplitter(':').apply { setString(enabled) }
        while (colon.hasNext()) {
            if (colon.next().contains(context.packageName)) return true
        }
        return false
    }

    fun canDrawOverlays(context: Context): Boolean = Settings.canDrawOverlays(context)

    fun isNotificationListenerEnabled(context: Context): Boolean {
        val enabled = Settings.Secure.getString(context.contentResolver, "enabled_notification_listeners") ?: return false
        return enabled.contains(context.packageName)
    }
}
