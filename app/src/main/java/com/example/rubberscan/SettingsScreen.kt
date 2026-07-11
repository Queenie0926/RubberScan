package com.example.rubberscan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rubberscan.ui.theme.*
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SettingsScreen(
    onBack          : () -> Unit = {},
    onNavigate      : (String) -> Unit = {},
    notifications   : Boolean = true,
    onNotifications : (Boolean) -> Unit = {},
    diseaseAlerts   : Boolean = true,
    onDiseaseAlerts : (Boolean) -> Unit = {},
    autoReconnect   : Boolean = true,
    onAutoReconnect : (Boolean) -> Unit = {},
    onClearRecords  : (onDone: () -> Unit) -> Unit = {}
) {
    val context = LocalContext.current
    var storageUsed         by remember { mutableStateOf(getStorageUsed(context)) }
    var showClearDialog     by remember { mutableStateOf(false) }
    var showClearedNotif    by remember { mutableStateOf(false) }
    val packageInfo = remember {
        context.packageManager.getPackageInfo(context.packageName, 0)
    }
    val appVersion = remember { packageInfo.versionName ?: "1.0.0" }

    // ── Clear records confirmation dialog ────────────────
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear All Records?", fontWeight = FontWeight.Bold) },
            text  = { Text("This will permanently delete all scan history. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearDialog = false
                        onClearRecords {
                            showClearedNotif = true
                            storageUsed = getStorageUsed(context)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFEF5350))
                ) {
                    Text("Delete All", fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBg)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ──────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenDark)
                    .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text("Settings", color = Color.White,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── BLE Sensor ───────────────────────────────
                SettingsSectionCard(title = "BLE Sensor") {
                    SettingsToggleRow(
                        icon            = Icons.Default.Refresh,
                        iconTint        = GreenDark,
                        iconBg          = GreenLight,
                        label           = "Auto-reconnect",
                        checked         = autoReconnect,
                        onCheckedChange = onAutoReconnect
                    )
                }

                // ── Notifications ────────────────────────────
                SettingsSectionCard(title = "Notifications") {
                    SettingsToggleRow(
                        icon            = Icons.Default.Notifications,
                        iconTint        = OrangeDark,
                        iconBg          = OrangeLight,
                        label           = "Push Notifications",
                        checked         = notifications,
                        onCheckedChange = onNotifications
                    )
                    HorizontalDivider(color = BorderGray)
                    SettingsEmojiToggleRow(
                        emoji           = "🍂",
                        iconBg          = OrangeLight,
                        label           = "Disease Alerts",
                        checked         = diseaseAlerts,
                        onCheckedChange = onDiseaseAlerts
                    )
                }

                // ── Data Storage ─────────────────────────────
                SettingsSectionCard(title = "Data Storage") {
                    SettingsInfoRow(
                        icon     = Icons.Default.Storage,
                        iconTint = SlateGray,
                        iconBg   = SlateGrayLight,
                        label    = "Storage Used",
                        info     = storageUsed
                    )
                    HorizontalDivider(color = BorderGray)
                    SettingsEmojiNavRow(
                        emoji   = "🗑️",
                        iconBg  = RedLight,
                        label   = "Clear Old Records",
                        onClick = { showClearDialog = true }
                    )
                }

                // ── About ────────────────────────────────────
                SettingsSectionCard(title = "About System") {
                    SettingsInfoRow(
                        icon     = Icons.Default.Info,
                        iconTint = GreenDark,
                        iconBg   = GreenLight,
                        label    = "App Version",
                        info     = appVersion
                    )
                    HorizontalDivider(color = BorderGray)
                    SettingsEmojiNavRow(
                        emoji   = "📄",
                        iconBg  = SurfaceGray,
                        label   = "Privacy Policy",
                        onClick = { onNavigate("privacy-policy") }
                    )
                }

                Spacer(Modifier.height(8.dp))
            }
        }

        // ── Records cleared toast ────────────────────────────
        if (showClearedNotif) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { showClearedNotif = false }) {
                        Text("OK", color = Color.White)
                    }
                },
                containerColor = Color(0xFF323232)
            ) {
                Text("All scan records deleted.", color = Color.White)
            }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(3000.milliseconds)
                showClearedNotif = false
            }
        }
    }
}

// ── Storage calculator ──────────────────────────────────────
fun getStorageUsed(context: android.content.Context): String {
    var totalBytes = 0L
    val dbDir = context.getDatabasePath("rubber_scan_db").parentFile
    dbDir?.listFiles()?.forEach { totalBytes += it.length() }
    context.cacheDir.listFiles()?.forEach { totalBytes += it.length() }
    context.filesDir.listFiles()?.forEach { totalBytes += it.length() }
    return when {
        totalBytes < 1024        -> "$totalBytes B"
        totalBytes < 1024 * 1024 -> "${"%.1f".format(totalBytes / 1024f)} KB"
        else                     -> "${"%.1f".format(totalBytes / (1024f * 1024f))} MB"
    }
}

// ── Settings Section Card ───────────────────────────────────
@Composable
fun SettingsSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title.uppercase(),
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = androidx.compose.ui.unit.TextUnit(
                0.07f, androidx.compose.ui.unit.TextUnitType.Em
            ),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(content = content)
        }
    }
}

// ── Toggle Row ──────────────────────────────────────────────
@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint,
                modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = TextPrimary,
            modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = GreenDark,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = BorderLight
            )
        )
    }
}

// ── Emoji Toggle Row ────────────────────────────────────────
@Composable
fun SettingsEmojiToggleRow(
    emoji: String,
    iconBg: Color,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = TextPrimary,
            modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor   = Color.White,
                checkedTrackColor   = GreenDark,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = BorderLight
            )
        )
    }
}

// ── Info Row ────────────────────────────────────────────────
@Composable
fun SettingsInfoRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    info: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint,
                modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = TextPrimary,
            modifier = Modifier.weight(1f))
        Text(info, color = TextMuted2, fontSize = 13.sp)
    }
}

// ── Emoji Nav Row ───────────────────────────────────────────
@Composable
fun SettingsEmojiNavRow(
    emoji: String,
    iconBg: Color,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 16.sp)
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = TextPrimary,
            modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = TextMuted2, modifier = Modifier.size(16.dp))
    }
}

