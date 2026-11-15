package com.jarvis.android.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.jarvis.android.ui.Screen

@Composable
fun PermissionsHubScreen(nav: NavController) {
    val ctx = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("دسترسی‌های جارویس", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20).copy(alpha = 0.15f)), shape = RoundedCornerShape(16.dp)) {
                Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.VerifiedUser, null, tint = Color(0xFF00E676))
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("حریم خصوصی شما محفوظ است", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("هیچ داده‌ای بدون اجازه شما ارسال نمی‌شود. همه دسترسی‌ها قابل لغو هستند.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                }
            }

            PermissionCard(
                icon = "🎤", title = "میکروفون", desc = "برای دستیار صوتی و Wake Word «هی جارویس»",
                required = true, status = "در انتظار",
                onEnable = { /* request RECORD_AUDIO */ }
            )
            PermissionCard(
                icon = "♿", title = "دسترسی‌پذیری", desc = "خواندن صفحه، کلیک، تایپ، اتوماسیون هر اپ — قلب جارویس",
                required = false, status = "غیرفعال — اختیاری",
                onEnable = { ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
            )
            PermissionCard(
                icon = "🖼️", title = "نمایش روی سایر برنامه‌ها", desc = "کره شناور HUD — همیشه در دسترس",
                required = false, status = "غیرفعال",
                onEnable = { ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) }
            )
            PermissionCard(
                icon = "🔔", title = "دسترسی به اعلان‌ها", desc = "خواندن اعلان‌ها و پاسخ خودکار",
                required = false, status = "غیرفعال",
                onEnable = { ctx.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)) }
            )
            PermissionCard(
                icon = "📍", title = "موقعیت مکانی", desc = "اتوماسیون مبتنی بر مکان",
                required = false, status = "غیرفعال",
                onEnable = {}
            )
            PermissionCard(
                icon = "📇", title = "مخاطبین / پیامک / تماس", desc = "تماس و پیام با نام مخاطب",
                required = false, status = "غیرفعال",
                onEnable = {}
            )

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { nav.navigate(Screen.Chat.route) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00D4FF))
            ) { Text("ادامه → رفتن به چت", color = Color.Black, fontWeight = FontWeight.Bold) }

            Text("💡 می‌تونی همه دسترسی‌ها رو بعداً در تنظیمات فعال کنی. جارویس بدون آن‌ها هم به‌صورت چت کار می‌کند.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun PermissionCard(icon: String, title: String, desc: String, required: Boolean, status: String, onEnable: () -> Unit) {
    Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF141A2E))) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, fontSize = 22.sp)
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        if (required) {
                            Spacer(Modifier.width(6.dp))
                            Badge(containerColor = Color(0xFFFF5252)) { Text("ضروری", fontSize = 9.sp, color = Color.White) }
                        } else {
                            Spacer(Modifier.width(6.dp))
                            Badge(containerColor = Color.White.copy(alpha = 0.15f)) { Text("اختیاری", fontSize = 9.sp) }
                        }
                    }
                    Text(desc, fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(status, fontSize = 11.sp, color = Color(0xFFFFAB00))
                Spacer(Modifier.weight(1f))
                FilledTonalButton(onClick = onEnable, shape = RoundedCornerShape(10.dp)) { Text("فعال‌سازی", fontSize = 12.sp) }
            }
        }
    }
}
