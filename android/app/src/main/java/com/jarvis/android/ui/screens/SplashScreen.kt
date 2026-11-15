package com.jarvis.android.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jarvis.android.ui.Screen
import com.jarvis.android.ui.theme.JarvisColors
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(nav: NavController) {
    val infinite = rememberInfiniteTransition(label = "splash")
    val scale by infinite.animateFloat(
        initialValue = 1f, targetValue = 1.06f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )
    LaunchedEffect(Unit) {
        delay(1400)
        nav.navigate(Screen.Onboarding.route) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(JarvisColors.GradientDark)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(Brush.radialGradient(JarvisColors.GradientPrimary)),
                contentAlignment = Alignment.Center
            ) {
                Text("🤖", fontSize = 64.sp)
            }
            Text("JARVIS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 34.sp, letterSpacing = 6.sp)
            Text("یار هوشمند شما", color = JarvisColors.NeonCyan, fontSize = 14.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator(color = JarvisColors.NeonCyan, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
        }
        Text(
            "v2.2 • ساخته شده با ❤️ برای اندروید",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp)
        )
    }
}
