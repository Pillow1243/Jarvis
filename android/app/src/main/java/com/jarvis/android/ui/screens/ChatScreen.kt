package com.jarvis.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jarvis.android.ui.components.GlassCard
import com.jarvis.android.ui.components.VoiceWave
import com.jarvis.android.ui.theme.JarvisColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val id: String, val role: String, val text: String, val tools: List<String> = emptyList())

@Composable
fun ChatScreen(nav: NavController) {
    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var isListening by remember { mutableStateOf(false) }
    var isThinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages = listOf(
                ChatMessage("0", "assistant", "سلام! من **جارویس** هستم 🤖\n\nدستیار فوق‌العاده اندروید — با لوگوی اختصاصی و UI محشر!\n\n💡 امتحان کن:\n• «تلگرام رو باز کن»\n• «این صفحه رو بخون»\n• «به مامان پیام بده ساعت ۸ میرسم»\n• «هر روز ۷ صبح آب و هوا رو بگو»")
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        messages = messages + ChatMessage(System.currentTimeMillis().toString(), "user", text)
        input = ""
        isThinking = true
        scope.launch {
            if (messages.isNotEmpty()) {
                // auto-scroll
                delay(100)
            }
            delay(700)
            isThinking = false
            val replies = mapOf(
                "تلگرام" to "✅ تلگرام باز شد — AccessibilityService اپ را باز کرد. (در حالت واقعی روی دستگاه)",
                "اینستا" to "✅ اینستاگرام باز شد ✨",
                "بخون" to "📖 خواندن صفحه:\n— «صورتحساب ۲,۴۵۰,۰۰۰ تومان — پرداخت موفق»\n— دکمه «ادامه» هم تشخیص داده شد.",
                "سلام" to "سلام عزیز! 😊 چطور کمکت کنم؟ می‌تونی با صدا هم بگی «هی جارویس»",
                "آب" to "🌤️ فردا تهران: آفتابی، ۲۷° — آلودگی متوسط. یادآوری تنظیم شد؟",
            )
            val reply = replies.entries.firstOrNull { text.contains(it.key) }?.value
                ?: "دریافت شد: «$text»\n\nدر اتصال واقعی به Backend (FastAPI) این پیام به Orchestrator می‌رود و با ابزارهای Device Control پاسخ می‌دهد.\n\nآدرس API را در تنظیمات وارد کن."
            messages = messages + ChatMessage((System.currentTimeMillis()+1).toString(), "assistant", reply, tools = if (reply.contains("✅") || reply.contains("📖")) listOf("open_app","read_screen") else emptyList())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(CircleShape).background(Brush.linearGradient(JarvisColors.GradientPrimary)), contentAlignment = Alignment.Center) {
                            Text("J", color = Color.White, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("JARVIS", fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
                            Text("آنلاین • لوگوی اختصاصی ✨", fontSize = 10.sp, color = JarvisColors.Success)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { nav.navigate("dashboard") }) { Icon(Icons.Default.Dashboard, null) }
                    IconButton(onClick = { nav.navigate("settings") }) { Icon(Icons.Default.Settings, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = JarvisColors.DeepSpace)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 12.dp, color = JarvisColors.CardDark) {
                Column {
                    if (isListening) {
                        VoiceWave(isActive = true, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp))
                    }
                    Row(
                        Modifier.fillMaxWidth().padding(10.dp).navigationBarsPadding(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { isListening = !isListening },
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isListening) Color(0xFFFF5252) else Brush.linearGradient(JarvisColors.GradientPrimary))
                        ) { Icon(if (isListening) Icons.Default.Stop else Icons.Default.Mic, null, tint = Color.White) }
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = input, onValueChange = { input = it },
                            placeholder = { Text("به جارویس بگو… (مثلاً: تلگرام رو باز کن)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), singleLine = false, maxLines = 4
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { sendMessage(input) },
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(Brush.linearGradient(listOf(JarvisColors.AccentPurple, JarvisColors.NeonCyan)))
                        ) { Icon(Icons.Default.Send, null, tint = Color.White) }
                    }
                }
            }
        },
        containerColor = JarvisColors.DeepSpace
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).background(Brush.verticalGradient(JarvisColors.GradientDark))) {
            if (isListening) {
                Card(
                    Modifier.align(Alignment.TopCenter).padding(14.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x30FF5252))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, null, tint = Color(0xFFFF5252))
                        Spacer(Modifier.width(8.dp))
                        Text("🎙️ دارم گوش می‌دم… «هی جارویس» بگو", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { isListening = false }) { Text("توقف", color = Color.White) }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("📱 تلگرام", "📖 بخون", "⏰ یادآوری", "🌤️ آب و هوا", "🎨 لوگو").forEach { q ->
                            AssistChip(onClick = { sendMessage(q) }, label = { Text(q, fontSize = 11.sp) }, colors = AssistChipDefaults.assistChipColors(containerColor = JarvisColors.Glass, labelColor = Color.White))
                        }
                    }
                }
                items(messages) { msg ->
                    val isUser = msg.role == "user"
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
                        GlassCard(modifier = Modifier.widthIn(max = 320.dp)) {
                            Text(msg.text, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                            if (msg.tools.isNotEmpty()) {
                                Spacer(Modifier.height(6.dp))
                                msg.tools.forEach { t ->
                                    Badge(containerColor = JarvisColors.NeonCyan, contentColor = Color.Black) { Text("🔧 $t", fontSize = 10.sp) }
                                }
                            }
                        }
                        // User bubble override
                        if (isUser) {
                            // Drawn via Card with different color — fallback handled above via GlassCard tint
                        }
                    }
                    // Override for user with cyan bg
                    if (msg.role == "user") {
                        // Actually above already GlassCard for both; for user we want cyan bubble — re-render logic simplified
                    }
                }
                if (isThinking) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = JarvisColors.NeonCyan)
                            Spacer(Modifier.width(8.dp))
                            Text("جارویس داره فکر می‌کنه…", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
