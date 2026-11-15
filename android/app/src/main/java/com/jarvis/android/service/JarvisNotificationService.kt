package com.jarvis.android.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import timber.log.Timber

class JarvisNotificationService : NotificationListenerService() {
    companion object {
        var lastNotifications: List<String> = emptyList()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn?.let {
            val title = it.notification.extras.getString("android.title") ?: ""
            val text = it.notification.extras.getString("android.text") ?: ""
            Timber.i("🔔 Notification: [$title] $text from ${it.packageName}")
            lastNotifications = (listOf("[$title] $text — ${it.packageName}") + lastNotifications).take(50)
        }
    }
    override fun onNotificationRemoved(sbn: StatusBarNotification?) {}
}
