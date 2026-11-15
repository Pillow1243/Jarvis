package com.jarvis.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(nav: NavController) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("داشبورد جارویس", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("⚡", "1,247", "دستور اجرا شده", Color(0xFF00D4FF), Modifier.weight(1f))
                StatCard("🎙️", "342", "مکالمه صوتی", Color(0xFF7F52FF), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                StatCard("⏱️", "1.2s", "میانگین پاسخ", Color(0xFF00E676), Modifier.weight(1f))
                StatCard("🔋", "2.1%", "مصرف باتری/روز", Color(0xFFFFAB00), Modifier.weight(1f))
            }
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF141A2E))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📊 مصرف انرژی (الهام از OpenJarvis)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("جارویس به‌صورت هوشمند بین مدل محلی (Ollama) و ابری (GPT-4o) مسیریابی می‌کند تا هزینه و مصرف باتری بهینه بماند.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    LinearProgressIndicator(progress = { 0.72f }, modifier = Modifier.fillMaxWidth().height(8.dp))
                    Text("72% درخواست‌ها با مدل محلی پاسخ داده شد — صرفه‌جویی: ~$4.30", fontSize = 11.sp, color = Color(0xFF00E676))
                }
            }
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF141A2E))) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🕘 آخرین فعالیت‌ها", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    listOf("✅ تلگرام باز شد — ۲ دقیقه پیش", "📖 صفحه دیجی‌کالا خوانده شد — ۵ دقیقه پیش", "⏰ یادآوری «قرص» تنظیم شد — ۱ ساعت پیش").forEach {
                        Text("• $it", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(icon: String, value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF141A2E)), modifier = modifier) {
        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 20.sp)
            Text(value, fontWeight = FontWeight.Black, fontSize = 18.sp, color = color)
            Text(label, fontSize = 10.sp, color = Color.White.copy(alpha = 0.6f))
        }
    }
}
