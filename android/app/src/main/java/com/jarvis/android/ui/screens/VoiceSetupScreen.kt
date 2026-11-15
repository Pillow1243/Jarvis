package com.jarvis.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceSetupScreen(nav: NavController) {
    var listening by remember { mutableStateOf(false) }
    Scaffold(topBar = {
        TopAppBar(title = { Text("تنظیم صدا", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("🎙️", fontSize = 64.sp)
            Text("تشخیص صدای «هی جارویس»", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("برای تست، دکمه زیر را بزن و بگو: «هی جارویس، سلام»", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
            Button(
                onClick = { listening = !listening },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (listening) Color(0xFFFF5252) else Color(0xFF00D4FF))
            ) { Text(if (listening) "⏹️ توقف گوش دادن" else "🎤 شروع گوش دادن", color = Color.White, fontWeight = FontWeight.Bold) }
            if (listening) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("دارم گوش می‌دم… «هی جارویس» بگو", color = Color(0xFFFF5252), fontSize = 12.sp)
            }
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF141A2E))) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("نکات:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• در محیط آرام تست کن\n• با فاصله ۳۰ سانتی از گوشی صحبت کن\n• می‌تونی Wake Word را در تنظیمات خاموش کنی", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    }
}
