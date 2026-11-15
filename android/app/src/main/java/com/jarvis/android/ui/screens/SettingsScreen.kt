package com.jarvis.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(nav: NavController) {
    var apiUrl by remember { mutableStateOf("https://api.jarvis.example.com") }
    var selectedVoice by remember { mutableStateOf("فارسی - طبیعی") }
    var wakeWord by remember { mutableStateOf(true) }

    Scaffold(topBar = {
        TopAppBar(title = { Text("تنظیمات", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } })
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("🔌 اتصال به سرور", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            OutlinedTextField(value = apiUrl, onValueChange = { apiUrl = it }, label = { Text("آدرس Backend API") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Button(onClick = {}, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF))) {
                Text("تست اتصال", color = Color.Black)
            }

            HorizontalDivider()
            Text("🎙️ صدا", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Wake Word «هی جارویس»", fontSize = 13.sp)
                Switch(checked = wakeWord, onCheckedChange = { wakeWord = it })
            }
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(value = selectedVoice, onValueChange = {}, readOnly = true, label = { Text("صدای TTS") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth())
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("فارسی - طبیعی", "فارسی - مردانه", "English - Natural").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = { selectedVoice = it; expanded = false })
                    }
                }
            }

            HorizontalDivider()
            Text("ℹ️ درباره", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF141A2E))) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("JARVIS Android v2.1.0", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Apache 2.0 • الهام‌گرفته از OpenJarvis (Stanford)", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                    Text("github.com/Pillow1243/Jarvis", fontSize = 11.sp, color = Color(0xFF00D4FF))
                }
            }
            Text("ساخته شده با ❤️ برای اندروید", fontSize = 11.sp, color = Color.White.copy(alpha = 0.4f))
        }
    }
}
