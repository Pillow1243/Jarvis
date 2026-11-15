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
import com.jarvis.android.ui.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val id: String, val role: String, val text: String, val isThinking: Boolean = false, val tools: List<String> = emptyList())

@Composable
fun ChatScreen(nav: NavController) {
    var input by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var isListening by remember { mutableStateOf(false) }
    var isThinking by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Welcome
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages = listOf(ChatMessage("0", "assistant", "سلام! من **جارویس** هستم 🤖\n\nچطور می‌تونم کمکت کنم؟\n\n💡 امتحان کن:\n• «تلگرام رو باز کن»\n• «این صفحه رو بخون»\n• «به مامان پیام بده ساعت ۸ میرسم»\n• «هر روز ۷ صبح آب و هوا رو بگو»"))
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(System.currentTimeMillis().toString(), "user", text)
        messages = messages + userMsg
        input = ""
        isThinking = true
        // Simulate agent response (in prod: call backend via Retrofit/WebSocket)
        scope.launch {
            delay(800)
            isThinking = false
            val mockResponses = mapOf(
                "تلگرام" to "✅ تلگرام باز شد — در حالت واقعی AccessibilityService اپ را باز می‌کند.",
                "اینستاگرام" to "✅ اینستاگرام باز شد.",
                "بخون" to "📖 در حال خواندن صفحه...\n— متن صفحه: «صورتحساب ۲,۴۵۰,۰۰۰ تومان — پرداخت با موفقیت انجام شد»",
                "سلام" to "سلام عزیز! 😊 چطور می‌تونم کمکت کنم؟",
            )
            val reply = mockResponses.entries.firstOrNull { text.contains(it.key) }?.value
                ?: "دریافت شد: «$text»\n\nدر حالت متصل به سرور، این پیام به Backend (FastAPI) ارسال می‌شود و جارویس با ابزارهای Device Control پاسخ می‌دهد.\n\nبرای اتصال واقعی، در تنظیمات آدرس API را وارد کن."
            messages = messages + ChatMessage((System.currentTimeMillis()+1).toString(), "assistant", reply, tools = if (reply.contains("✅")) listOf("open_app") else emptyList())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(36.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Color(0xFF00D4FF), Color(0xFF7F52FF)))), contentAlignment = Alignment.Center) {
                            Text("J", color = Color.White, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text("JARVIS", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("آنلاین • همیشه گوش به زنگ", fontSize = 11.sp, color = Color(0xFF00E676))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { nav.navigate(Screen.Dashboard.route) }) { Icon(Icons.Default.Dashboard, null) }
                    IconButton(onClick = { nav.navigate(Screen.Settings.route) }) { Icon(Icons.Default.Settings, null) }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Row(
                    Modifier.fillMaxWidth().padding(10.dp).navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { isListening = !isListening },
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(if (isListening) Color(0xFFFF5252) else Color(0xFF00D4FF))
                    ) {
                        Icon(if (isListening) Icons.Default.Stop else Icons.Default.Mic, null, tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { Text("به جارویس بگو…", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = { sendMessage(input) },
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF7F52FF))
                    ) { Icon(Icons.Default.Send, null, tint = Color.White) }
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (isListening) {
                Card(
                    Modifier.align(Alignment.TopCenter).padding(16.dp).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF5252).copy(alpha = 0.15f))
                ) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Mic, null, tint = Color(0xFFFF5252))
                        Spacer(Modifier.width(8.dp))
                        Text("🎙️ دارم گوش می‌دم… «هی جارویس» بگو", color = Color(0xFFFF5252), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { isListening = false }) { Text("توقف") }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Quick actions
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("📱 تلگرام رو باز کن", "📖 صفحه رو بخون", "⏰ یادآوری بساز", "🌤️ آب و هوا").forEach { q ->
                            AssistChip(onClick = { sendMessage(q) }, label = { Text(q, fontSize = 11.sp) })
                        }
                    }
                }
                items(messages) { msg ->
                    val isUser = msg.role == "user"
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(18.dp, 18.dp, if (isUser) 4.dp else 18.dp, if (isUser) 18.dp else 4.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isUser) Color(0xFF00D4FF) else Color(0xFF141A2E)),
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Column(Modifier.padding(12.dp)) {
                                Text(msg.text, color = if (isUser) Color.Black else Color.White, fontSize = 13.sp, lineHeight = 18.sp)
                                if (msg.tools.isNotEmpty()) {
                                    Spacer(Modifier.height(6.dp))
                                    msg.tools.forEach { t ->
                                        AssistChip(onClick = {}, label = { Text("🔧 $t", fontSize = 10.sp) }, modifier = Modifier.padding(top = 2.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                if (isThinking) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color(0xFF00D4FF))
                            Spacer(Modifier.width(8.dp))
                            Text("جارویس داره فکر می‌کنه…", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
