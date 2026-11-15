package com.jarvis.android.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class AutomationItem(val id: String, val name: String, val trigger: String, val action: String, val enabled: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationsScreen(nav: NavController) {
    var items by remember {
        mutableStateOf(listOf(
            AutomationItem("1", "صبح بخیر ☀️", "هر روز ۷:۰۰", "آب‌وهوا + تقویم + ترافیک را با صدا بگو", true),
            AutomationItem("2", "یادآوری قرص 💊", "هر روز ۲۲:۰۰", "اعلان: قرصت رو خوردی؟", true),
            AutomationItem("3", "حالت شب 🌙", "۲۳:۰۰ — اتصال به وای‌فای خانه", "نور صفحه کم + بی‌صدا", false),
        ))
    }
    var showAdd by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf("") }
    var newAction by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("اتوماسیون‌ها", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = { showAdd = true }) { Icon(Icons.Default.Add, null) } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAdd = true },
                icon = { Icon(Icons.Default.AutoAwesome, null) },
                text = { Text("اتوماسیون جدید") },
                containerColor = Color(0xFF00D4FF), contentColor = Color.Black
            )
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF7F52FF).copy(alpha = 0.15f)), shape = RoundedCornerShape(14.dp)) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 20.sp)
                        Spacer(Modifier.width(10.dp))
                        Text("بگو: «هر روز ساعت ۷ آب و هوا رو بگو» — جارویس اتومات می‌سازد!", fontSize = 11.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
            items(items) { item ->
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF141A2E))) {
                    Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("⏰ ${item.trigger}", fontSize = 11.sp, color = Color(0xFF00D4FF))
                            Text("→ ${item.action}", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                        }
                        Switch(checked = item.enabled, onCheckedChange = { checked ->
                            items = items.map { if (it.id == item.id) it.copy(enabled = checked) else it }
                        })
                    }
                }
            }
        }
    }

    if (showAdd) {
        AlertDialog(
            onDismissRequest = { showAdd = false },
            title = { Text("اتوماسیون جدید") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("نام") }, singleLine = true)
                    OutlinedTextField(value = newAction, onValueChange = { newAction = it }, label = { Text("دستور (مثلاً: هر روز ۷ صبح ...)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (newName.isNotBlank()) {
                        items = items + AutomationItem(System.currentTimeMillis().toString(), newName, "دستی", newAction, true)
                        newName = ""; newAction = ""; showAdd = false
                    }
                }) { Text("ذخیره") }
            },
            dismissButton = { TextButton(onClick = { showAdd = false }) { Text("لغو") } }
        )
    }
}
