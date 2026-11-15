package com.jarvis.android.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jarvis.android.ui.Screen

@Composable
fun OnboardingScreen(nav: NavController) {
    val infinite = rememberInfiniteTransition(label = "pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(animation = tween(1200, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A0E1A), Color(0xFF141A2E), Color(0xFF1A237E))))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(32.dp))

        // Logo
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(listOf(Color(0xFF00D4FF), Color(0xFF7F52FF)))),
                contentAlignment = Alignment.Center
            ) {
                Text("🤖", fontSize = 56.sp)
            }
            Spacer(Modifier.height(20.dp))
            Text("JARVIS", style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 4.sp))
            Text("فوق‌العاده‌ترین دستیار هوشمند اندروید", color = Color(0xFF00D4FF), fontSize = 13.sp)
            Spacer(Modifier.height(8.dp))
            Text("قوی‌تر از OpenJarvis • با دسترسی کامل به گوشی", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }

        // Features
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            FeatureRow(icon = "🎙️", title = "دستیار صوتی هوشمند", desc = "بگو «هی جارویس» — همیشه گوش به زنگ")
            FeatureRow(icon = "📱", title = "کنترل کامل گوشی", desc = "هر اپ، هر صفحه، هر کلیک با دستور صوتی")
            FeatureRow(icon = "🧠", title = "هوش مصنوعی قدرتمند", desc = "GPT-4o / Claude / Gemini + On-Device")
            FeatureRow(icon = "⚡", title = "اتوماسیون نامحدود", desc = "هر روز، هر مکان، هر رویداد — خودکار")
        }

        // CTA
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                onClick = { nav.navigate(Screen.Permissions.route) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF))
            ) {
                Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("شروع کن — فعال‌سازی جارویس", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            TextButton(onClick = { nav.navigate(Screen.Chat.route) }) {
                Text("رد شدن و رفتن به چت →", color = Color.White.copy(alpha = 0.7f))
            }
            Text("v2.0.0 • Apache 2.0 • ساخته شده با ❤️", color = Color.White.copy(alpha = 0.3f), fontSize = 10.sp)
        }
    }
}

@Composable
private fun FeatureRow(icon: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.07f))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, fontSize = 26.sp, modifier = Modifier.padding(end = 12.dp))
        Column {
            Text(title, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(desc, color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
        }
    }
}
