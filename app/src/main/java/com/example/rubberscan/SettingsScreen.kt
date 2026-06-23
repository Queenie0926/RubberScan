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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens ──────────────────────────────────────────
private val SettingsGreenDark = Color(0xFF1B5E20)
private val SettingsPageBg    = Color(0xFFF1F8F1)
private val SettingsCardBg    = Color(0xFFFFFFFF)
private val SettingsTextMuted = Color(0xFF9CA3AF)

// ── Settings Screen ────────────────────────────────────────
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    var darkMode        by remember { mutableStateOf(false) }
    var notifications   by remember { mutableStateOf(true) }
    var diseaseAlerts   by remember { mutableStateOf(true) }
    var weatherAlerts   by remember { mutableStateOf(false) }
    var autoReconnect   by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsPageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SettingsGreenDark)
                .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 24.dp),
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

        // ── Sections ────────────────────────────────────────
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Appearance
            SettingsSectionCard(title = "Appearance") {
                SettingsToggleRow(
                    icon = Icons.Default.DarkMode,
                    iconTint = Color(0xFF3949AB),
                    iconBg = Color(0xFFE8EAF6),
                    label = "Dark Mode",
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }

            // BLE Sensor
            SettingsSectionCard(title = "BLE Sensor") {
                SettingsNavRow(
                    icon = Icons.Default.Bluetooth,
                    iconTint = Color(0xFF00695C),
                    iconBg = Color(0xFFE0F2F1),
                    label = "Manage Sensors",
                    onClick = { onNavigate("ble-pairing") }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsToggleRow(
                    icon = Icons.Default.Refresh,
                    iconTint = Color(0xFF1B5E20),
                    iconBg = Color(0xFFE8F5E9),
                    label = "Auto-reconnect",
                    checked = autoReconnect,
                    onCheckedChange = { autoReconnect = it }
                )
            }

            // Notifications
            SettingsSectionCard(title = "Notifications") {
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    iconTint = Color(0xFFE65100),
                    iconBg = Color(0xFFFFF3E0),
                    label = "Push Notifications",
                    checked = notifications,
                    onCheckedChange = { notifications = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsEmojiToggleRow(
                    emoji = "🍂",
                    iconBg = Color(0xFFFFF3E0),
                    label = "Disease Alerts",
                    checked = diseaseAlerts,
                    onCheckedChange = { diseaseAlerts = it }
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsEmojiToggleRow(
                    emoji = "🌧️",
                    iconBg = Color(0xFFE3F2FD),
                    label = "Weather Alerts",
                    checked = weatherAlerts,
                    onCheckedChange = { weatherAlerts = it }
                )
            }

            // Data Storage
            SettingsSectionCard(title = "Data Storage") {
                SettingsInfoRow(
                    icon = Icons.Default.Storage,
                    iconTint = Color(0xFF546E7A),
                    iconBg = Color(0xFFECEFF1),
                    label = "Storage Used",
                    info = "142 MB"
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsEmojiNavRow(
                    emoji = "🗑️",
                    iconBg = Color(0xFFFFEBEE),
                    label = "Clear Old Records",
                    onClick = { }
                )
            }

            // Language
            SettingsSectionCard(title = "Language") {
                SettingsNavRowWithInfo(
                    icon = Icons.Default.Language,
                    iconTint = Color(0xFF0D47A1),
                    iconBg = Color(0xFFE3F2FD),
                    label = "Display Language",
                    info = "English"
                )
            }

            // About
            SettingsSectionCard(title = "About System") {
                SettingsInfoRow(
                    icon = Icons.Default.Info,
                    iconTint = Color(0xFF1B5E20),
                    iconBg = Color(0xFFE8F5E9),
                    label = "App Version",
                    info = "1.0.0"
                )
                HorizontalDivider(color = Color(0xFFF0F0F0))
                SettingsEmojiNavRow(
                    emoji = "📄",
                    iconBg = Color(0xFFF3F4F6),
                    label = "Privacy Policy",
                    onClick = { }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Settings Section Card ───────────────────────────────────
@Composable
fun SettingsSectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            title.uppercase(),
            color = Color(0xFF757575),
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = androidx.compose.ui.unit.TextUnit(0.07f,
                androidx.compose.ui.unit.TextUnitType.Em),
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SettingsCardBg),
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
            fontSize = 14.sp, color = Color(0xFF1C1C1C),
            modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SettingsGreenDark,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB)
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
            fontSize = 14.sp, color = Color(0xFF1C1C1C),
            modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SettingsGreenDark,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFD1D5DB)
            )
        )
    }
}

// ── Nav Row ─────────────────────────────────────────────────
@Composable
fun SettingsNavRow(
    icon: ImageVector,
    iconTint: Color,
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
            Icon(icon, contentDescription = null, tint = iconTint,
                modifier = Modifier.size(18.dp))
        }
        Spacer(Modifier.width(12.dp))
        Text(label, fontWeight = FontWeight.Medium,
            fontSize = 14.sp, color = Color(0xFF1C1C1C),
            modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = SettingsTextMuted, modifier = Modifier.size(16.dp))
    }
}

// ── Nav Row with Info ───────────────────────────────────────
@Composable
fun SettingsNavRowWithInfo(
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
            fontSize = 14.sp, color = Color(0xFF1C1C1C),
            modifier = Modifier.weight(1f))
        Text(info, color = SettingsTextMuted, fontSize = 13.sp)
        Spacer(Modifier.width(4.dp))
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = SettingsTextMuted, modifier = Modifier.size(16.dp))
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
            fontSize = 14.sp, color = Color(0xFF1C1C1C),
            modifier = Modifier.weight(1f))
        Text(info, color = SettingsTextMuted, fontSize = 13.sp)
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
            fontSize = 14.sp, color = Color(0xFF1C1C1C),
            modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null,
            tint = SettingsTextMuted, modifier = Modifier.size(16.dp))
    }
}