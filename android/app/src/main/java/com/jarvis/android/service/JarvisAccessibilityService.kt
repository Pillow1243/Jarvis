package com.jarvis.android.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import timber.log.Timber

/**
 * 💥 The Power Core — Full screen control via AccessibilityService.
 * Inspired by OpenJarvis's system access, rebuilt for Android.
 *
 * With user's explicit permission, this service can:
 * - Read any screen (texts, buttons, fields)
 * - Tap / Swipe / Scroll / Type
 * - Open apps, go back/home, notifications
 */
class JarvisAccessibilityService : AccessibilityService() {

    companion object {
        var instance: JarvisAccessibilityService? = null
            private set
        var lastScreenText: String = ""
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Timber.i("♿ JarvisAccessibilityService connected — full control enabled")
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            // Keep last screen snapshot for read_screen
            try {
                val root = rootInActiveWindow ?: return
                try {
                    lastScreenText = dumpNode(root, 0)
                } finally {
                    @Suppress("DEPRECATION")
                    root.recycle()
                }
            } catch (_: Exception) { /* keep last snapshot */ }
        }
    }

    override fun onInterrupt() {}

    // ── Public API called by Chat/API ──────────────────────────────

    fun tap(x: Int, y: Int): Boolean {
        val path = Path().apply { moveTo(x.toFloat(), y.toFloat()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 80))
            .build()
        var result = false
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(g: GestureDescription?) { result = true }
        }, null)
        Thread.sleep(120)
        return result
    }

    fun tapOnText(text: String): Boolean {
        val root = rootInActiveWindow ?: return false
        return try {
            val node = findByText(root, text) ?: return false
            try {
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            } finally {
                if (node !== root) {
                    @Suppress("DEPRECATION")
                    node.recycle()
                }
            }
        } finally {
            @Suppress("DEPRECATION")
            root.recycle()
        }
    }

    fun typeText(text: String, submit: Boolean = false): Boolean {
        val root = rootInActiveWindow ?: return false
        return try {
            val focused = findFocusedEditText(root) ?: return false
            try {
                val args = Bundle().apply { putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text) }
                val ok = focused.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
                if (submit && ok) {
                    focused.performAction(AccessibilityNodeInfo.ACTION_IME_ENTER)
                }
                ok
            } finally {
                if (focused !== root) {
                    @Suppress("DEPRECATION")
                    focused.recycle()
                }
            }
        } finally {
            @Suppress("DEPRECATION")
            root.recycle()
        }
    }

    fun swipe(x1: Int, y1: Int, x2: Int, y2: Int, duration: Int = 300): Boolean {
        val path = Path().apply { moveTo(x1.toFloat(), y1.toFloat()); lineTo(x2.toFloat(), y2.toFloat()) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration.toLong()))
            .build()
        dispatchGesture(gesture, null, null)
        Thread.sleep((duration + 50).toLong())
        return true
    }

    fun swipeDirection(direction: String): Boolean {
        val metrics = resources.displayMetrics
        val w = metrics.widthPixels; val h = metrics.heightPixels
        return when (direction) {
            "up" -> swipe(w/2, h*3/4, w/2, h/4)
            "down" -> swipe(w/2, h/4, w/2, h*3/4)
            "left" -> swipe(w*3/4, h/2, w/4, h/2)
            "right" -> swipe(w/4, h/2, w*3/4, h/2)
            else -> false
        }
    }

    fun goBack(): Boolean = performGlobalAction(GLOBAL_ACTION_BACK)
    fun goHome(): Boolean = performGlobalAction(GLOBAL_ACTION_HOME)
    fun openNotifications(): Boolean = performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    fun openRecents(): Boolean = performGlobalAction(GLOBAL_ACTION_RECENTS)

    fun openApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
                ?: Intent().apply { setPackage(packageName) }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            true
        } catch (e: Exception) {
            Timber.e(e, "openApp failed: $packageName")
            false
        }
    }

    fun readScreen(): String {
        val root = rootInActiveWindow ?: return lastScreenText.ifEmpty { "No window" }
        return try {
            dumpNode(root, 0)
        } finally {
            @Suppress("DEPRECATION")
            root.recycle()
        }
    }

    // ── Helpers ───────────────────────────────────────────────────

    private fun dumpNode(node: AccessibilityNodeInfo, depth: Int): String {
        val sb = StringBuilder()
        dumpNodeInto(node, depth, sb)
        return sb.toString()
    }

    private fun dumpNodeInto(node: AccessibilityNodeInfo, depth: Int, sb: StringBuilder) {
        val indent = "  ".repeat(depth)
        val text = node.text?.toString()
        val desc = node.contentDescription?.toString()
        val id = node.viewIdResourceName
        val cls = node.className?.toString()?.substringAfterLast('.')
        if (!text.isNullOrBlank() || !desc.isNullOrBlank() || cls == "Button") {
            sb.appendLine("$indent- [$cls] text=\"$text\" desc=\"$desc\" id=$id clickable=${node.isClickable}")
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            try {
                dumpNodeInto(child, depth + 1, sb)
            } finally {
                @Suppress("DEPRECATION")
                child.recycle()
            }
        }
    }

    private fun findByText(node: AccessibilityNodeInfo, text: String): AccessibilityNodeInfo? {
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true) return node
        if (node.contentDescription?.toString()?.contains(text, ignoreCase = true) == true) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findByText(child, text)
            if (found != null) return found
            @Suppress("DEPRECATION")
            child.recycle()
        }
        return null
    }

    private fun findFocusedEditText(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isFocused && node.className?.contains("EditText") == true) return node
        if (node.isEditable) return node
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findFocusedEditText(child)
            if (found != null) return found
            @Suppress("DEPRECATION")
            child.recycle()
        }
        return null
    }
}
