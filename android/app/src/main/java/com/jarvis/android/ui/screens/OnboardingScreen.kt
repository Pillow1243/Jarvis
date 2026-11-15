package com.jarvis.android.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jarvis.android.ui.Screen
import com.jarvis.android.ui.theme.JarvisColors
import kotlinx.coroutines.launch

data class OnBoardPage(val icon: String, val title: String, val desc: String, val gradient: List<Color>)

@Composable
fun OnboardingScreen(nav: NavController) {
    val pages = listOf(
        OnBoardPage("🎙️", "بگو، جارویس می‌شنوه", "Wake Word «هی جارویس» — حتی وقتی صفحه خاموشه، همیشه گوش به زنگه. فارسی و انگلیسی.", listOf(Color(0xFF00D4FF), Color(0xFF0091EA))),
        OnBoardPage("📱", "کنترل کامل گوشی", "هر اپ، هر صفحه، هر دکمه — با صدا باز کن، بزن، بنویس، اسکرول کن. بدون لمس.", listOf(Color(0xFF7F52FF), Color(0xFF00D4FF))),
        OnBoardPage("🧠", "هوش مصنوعی واقعی", "GPT-4o + Claude + Gemini + مدل محلی — مثل OpenJarvis، ولی روی موبایل و سریع‌تر.", listOf(Color(0xFFFF6B35), Color(0xFFFFAB00))),
        OnBoardPage("⚡", "اتوماسیون نامحدود", "«هر روز ۷ صبح آب‌وهوا رو بگو» — جارویس خودش می‌سازد و هر روز اجرا می‌کند.", listOf(Color(0xFF00E676), Color(0xFF00D4FF))),
    )
    val pager = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    val infinite = rememberInfiniteTransition(label = "pulse")
    val scale by infinite.animateFloat(
        initialValue = 1f, targetValue = 1.07f,
        animationSpec = infiniteRepeatable(tween(1200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(JarvisColors.GradientDark))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        // Top logo (real icon would be painterResource if bundled, fallback to emoji orb)
        Box(
            modifier = Modifier
                .size(96.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(Brush.radialGradient(JarvisColors.GradientPrimary)),
            contentAlignment = Alignment.Center
        ) { Text("🤖", fontSize = 48.sp) }
        Spacer(Modifier.height(12.dp))
        Text("JARVIS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 28.sp, letterSpacing = 5.sp)
        Text("یار هوشمند شما  •  فوق‌العاده‌ترین دستیار اندروید", color = JarvisColors.NeonCyan, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))

        HorizontalPager(state = pager, modifier = Modifier.weight(1f)) { idx ->
            val p = pages[idx]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.08f), Color.Transparent)))
                    .border(1.dp, JarvisColors.GlassBorder, RoundedCornerShape(24.dp))
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(p.gradient)),
                    contentAlignment = Alignment.Center
                ) { Text(p.icon, fontSize = 40.sp) }
                Spacer(Modifier.height(18.dp))
                Text(p.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(10.dp))
                Text(p.desc, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 20.sp)
            }
        }

        // Indicators
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            repeat(pages.size) { i ->
                Box(
                    modifier = Modifier
                        .width(if (pager.currentPage == i) 24.dp else 8.dp)
                        .height(8.dp)
                        .clip(CircleShape)
                        .background(if (pager.currentPage == i) JarvisColors.NeonCyan else Color.White.copy(alpha = 0.3f))
                )
            }
        }
        Spacer(Modifier.height(16.dp))

        val isLast = pager.currentPage == pages.lastIndex
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (!isLast) {
                OutlinedButton(
                    onClick = { nav.navigate(Screen.Chat.route) },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                ) { Text("رد شدن") }
                Button(
                    onClick = { scope.launch { pager.animateScrollToPage(pager.currentPage + 1) } },
                    modifier = Modifier.weight(1f).height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisColors.NeonCyan)
                ) {
                    Text("بعدی", color = Color.Black, fontWeight = FontWeight.Bold)
                    Icon(Icons.Default.ChevronRight, null, tint = Color.Black)
                }
            } else {
                Button(
                    onClick = { nav.navigate(Screen.Permissions.route) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = JarvisColors.NeonCyan)
                ) {
                    Icon(Icons.Default.RocketLaunch, null, tint = Color.Black)
                    Spacer(Modifier.width(8.dp))
                    Text("شروع کن — فعال‌سازی جارویس", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
        Spacer(Modifier.height(6.dp))
        TextButton(onClick = { nav.navigate(Screen.Chat.route) }) {
            Text("قبلاً نصب کردم → رفتن به چت", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
        }
    }
}
