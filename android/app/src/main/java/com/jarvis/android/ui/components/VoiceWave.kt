package com.jarvis.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import com.jarvis.android.ui.theme.JarvisColors
import kotlin.math.sin

@Composable
fun VoiceWave(isActive: Boolean, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "wave")
    val phase by infinite.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "phase"
    )
    Canvas(modifier = modifier.height(48.dp).fillMaxWidth()) {
        val w = size.width
        val h = size.height
        val bars = 24
        for (i in 0 until bars) {
            val x = (w / bars) * i + (w / bars / 2)
            val amp = if (isActive) {
                val s = sin(Math.toRadians((phase + i * 15).toDouble())).toFloat()
                (0.4f + s * 0.6f).coerceIn(0.2f, 1f) * h * 0.45f
            } else {
                h * 0.12f
            }
            val color = lerpColor(JarvisColors.NeonCyan, JarvisColors.AccentPurple, i / bars.toFloat())
            drawLine(
                color = color,
                start = Offset(x, h/2 - amp),
                end = Offset(x, h/2 + amp),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

private fun lerpColor(a: Color, b: Color, t: Float): Color {
    return Color(
        a.red + (b.red - a.red) * t,
        a.green + (b.green - a.green) * t,
        a.blue + (b.blue - a.blue) * t,
        1f
    )
}
