package com.jarvis.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
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
import com.jarvis.android.ui.Screen
import com.jarvis.android.ui.components.GlassCard
import com.jarvis.android.ui.components.VoiceWave
import com.jarvis.android.ui.theme.JarvisColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val id: String, val role: String, val text: String, val tools: List<String> = emptyList())

@OptIn(ExperimentalMaterial3Api::class)
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
                ChatMessage(
                    "welcome", "assistant",
                    "سلام! من جارویس هستم 🤖\n\nدستیار هوشمند اندروید شما.\n\n💡 امتحان کن:\n• «تلگرام رو باز کن»\n• «این صفحه رو بخون»\n• «به مامان پیام بده ساعت ۸ میرسم»\n• «هر روز ۷ صبح آب و هوا رو بگو»"
                )
            )
        }
    }

    // Auto-scroll to the newest message / thinking indicator
    LaunchedEffect(messages.size, isThinking) {
        val target = messages.size + (if (isThinking) 1 else 0)
        if (target > 0) listState.animateScrollToItem(target)
    }

    fun sendMessage(text: String) {
        val trimmed = text.trim()
        if (trimmed.isBlank()) return
        messages = messages + ChatMessage(System.currentTimeMillis().toString(), "user", trimmed)
        input = ""
        isThinking = true
        scope.launch {
            delay(700)
            isThinking = false
            val replies = mapOf(
                "تلگرام" to "✅ تلگرام باز شد — AccessibilityService اپ را باز کرد. (در حالت واقعی روی دستگاه)",
                "اینستا" to "✅ اینستاگرام باز شد ✨",
                "بخون" to "📖 خواندن صفحه:\n— «صورتحساب ۲,۴۵۰,۰۰۰ تومان — پرداخت موفق»\n— دکمه «ادامه» هم تشخیص داده شد.",
                "سلام" to "سلام عزیز! 😊 چطور کمکت کنم؟ می‌تونی با صدا هم بگی «هی جارویس»",
                "آب" to "🌤️ فردا تهران: آفتابی، ۲۷° — آلودگی متوسط. یادآوری تنظیم شد؟",
            )
            val reply = replies.entries.firstOrNull { trimmed.contains(it.key) }?.value
                ?: "دریافت شد: «$trimmed»\n\nدر اتصال واقعی به Backend (FastAPI) این پیام به Orchestrator می‌رود و با ابزارهای Device Control پاسخ می‌دهد.\n\nآدرس API را در تنظیمات وارد کن."
            val usedTools = if (reply.contains("✅")) listOf("open_app") else if (reply.contains("📖")) listOf("read_screen") else emptyList()
            messages = messages + ChatMessage((System.currentTimeMillis() + 1).toString(), "assistant", reply, tools = usedTools)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(36.dp).clip(CircleShape).background(Brush.linearGradient(JarvisColors.GradientPrimary)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("J", color = Color.White, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("JARVIS", fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 1.sp)
                            Text("آنلاین", fontSize = 10.sp, color = JarvisColors.Success)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { nav.navigate(Screen.Dashboard.route) }) {
                        Icon(Icons.Default.Dashboard, contentDescription = "داشبورد")
                    }
                    IconButton(onClick = { nav.navigate(Screen.Settings.route) }) {
                        Icon(Icons.Default.Settings, contentDescription = "تنظیمات")
                    }
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
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isListening) Modifier.background(Color(0xFFFF5252))
                                    else Modifier.background(Brush.linearGradient(JarvisColors.GradientPrimary))
                                )
                        ) {
                            Icon(
                                if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                                contentDescription = if (isListening) "توقف گوش دادن" else "گوش دادن",
                                tint = Color.White
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        OutlinedTextField(
                            value = input, onValueChange = { input = it },
                            placeholder = { Text("به جارویس بگو… (مثلاً: تلگرام رو باز کن)", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp), singleLine = false, maxLines = 4
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(
                            onClick = { sendMessage(input) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(JarvisColors.AccentPurple, JarvisColors.NeonCyan)))
                        ) { Icon(Icons.Default.Send, contentDescription = "ارسال", tint = Color.White) }
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
                        listOf("📱 تلگرام", "📖 بخون", "⏰ یادآوری", "🌤️ آب و هوا").forEach { q ->
                            AssistChip(
                                onClick = { sendMessage(q) },
                                label = { Text(q, fontSize = 11.sp) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = JarvisColors.Glass, labelColor = Color.White)
                            )
                        }
                    }
                }
                items(messages, key = { it.id }) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        if (isUser) {
                            Box(
                                Modifier
                                    .widthIn(max = 300.dp)
                                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp))
                                    .background(Brush.linearGradient(listOf(JarvisColors.NeonCyan, Color(0xFF0091EA))))
                                    .padding(12.dp)
                            ) {
                                Text(msg.text, color = Color(0xFF04222E), fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.Medium)
                            }
                        } else {
                            GlassCard(modifier = Modifier.widthIn(max = 320.dp)) {
                                Text(msg.text, color = Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                                if (msg.tools.isNotEmpty()) {
                                    Spacer(Modifier.height(6.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        msg.tools.forEach { t ->
                                            Badge(containerColor = JarvisColors.NeonCyan, contentColor = Color.Black) {
                                                Text("🔧 $t", fontSize = 10.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
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
