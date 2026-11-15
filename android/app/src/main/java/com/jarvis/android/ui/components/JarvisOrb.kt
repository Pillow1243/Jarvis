package com.jarvis.android.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun JarvisOrb(isListening: Boolean, modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "orb")
    val scale by infinite.animateFloat(
        initialValue = 1f, targetValue = if (isListening) 1.15f else 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "orbScale"
    )
    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(Brush.radialGradient(listOf(Color(0xFF00D4FF), Color(0xFF7F52FF)))),
        contentAlignment = Alignment.Center
    ) {
        Text("🎙️", fontSize = 32.sp)
    }
}
