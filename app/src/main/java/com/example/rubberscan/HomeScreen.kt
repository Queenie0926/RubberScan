package com.example.rubberscan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rubberscan.ui.theme.*
import java.util.Calendar
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop

// ── Bottom Nav Item model ───────────────────────────────────
private data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

// ── Data models ────────────────────────────────────────────
data class QuickAction(
    val icon: ImageVector,
    val label: String,
    val iconColor: Color,
    val bgColor: Color,
    val route: String
)

data class StatusCardData(
    val icon: ImageVector,
    val label: String,
    val value: String,
    val sub: String,
    val valueColor: Color,
    val bgColor: Color
)

data class RecentInspection(
    val date: String,
    val result: String,
    val confidence: String,
    val color: Color,
    val bg: Color
)

// ── Home Screen ────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigate     : (String) -> Unit = {},
    userName       : String = "",
    bleViewModel   : BleViewModel? = null,
    isGuest        : Boolean = false,
    notifViewModel : NotificationViewModel? = null
) {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val greeting = when (currentHour) {
        in 0..11  -> "Good morning"
        in 12..17 -> "Good afternoon"
        else      -> "Good evening"
    }

    val quickActions = listOf(
        QuickAction(Icons.Default.Bluetooth,             "Pair Sensor",   BlueDark,   BlueLight,   "ble-pairing"),
        QuickAction(Icons.Default.DocumentScanner,       "Scan Leaf",     GreenDark,  GreenLight,  "scan"),
        QuickAction(Icons.AutoMirrored.Filled.MenuBook,  "Disease Guide", OrangeDark, OrangeLight, "disease-guide"),
        QuickAction(Icons.Default.History,               "View History",  PurpleDark, PurpleLight, "history")
    )

    val bleState    by (bleViewModel?.bleState      ?: kotlinx.coroutines.flow.MutableStateFlow(BleState.IDLE)).collectAsState()
    val bleTemp     by (bleViewModel?.temperature   ?: kotlinx.coroutines.flow.MutableStateFlow<Float?>(null)).collectAsState()
    val bleHumidity by (bleViewModel?.humidity      ?: kotlinx.coroutines.flow.MutableStateFlow<Float?>(null)).collectAsState()
    val bleName     by (bleViewModel?.connectedName ?: kotlinx.coroutines.flow.MutableStateFlow("")).collectAsState()

    val notifications by (notifViewModel?.notifications
        ?: kotlinx.coroutines.flow.MutableStateFlow<List<AppNotification>>(emptyList())).collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var showNotifPanel by remember { mutableStateOf(false) }
    var showSensorDialog by remember { mutableStateOf(false) }
    var selectedSensorNotif by remember { mutableStateOf<AppNotification?>(null) }

    val isConnected  = bleState == BleState.CONNECTED
    val tempText     = if (bleTemp != null) "%.1f°C".format(bleTemp) else "—"
    val humText      = if (bleHumidity != null) "%.1f%%".format(bleHumidity) else "—"
    val connSubtitle = if (isConnected) bleName.ifBlank { "RubberSense" } else "Not paired"

    val statusCards = listOf(
        StatusCardData(Icons.Default.Bluetooth,  "Sensor Connection", if (isConnected) "Connected" else "Disconnected", connSubtitle, if (isConnected) GreenDark else GreenMid, GreenLight),
        StatusCardData(Icons.Default.Thermostat, "Temperature",       tempText,    if (bleTemp != null) "Normal range" else "Pair sensor first", OrangeDark, OrangeLight),
        StatusCardData(Icons.Default.WaterDrop,  "Humidity",          humText,     if (bleHumidity != null) "Moderate"    else "Pair sensor first", BlueDark,   BlueLight),
        StatusCardData(Icons.Default.Warning,    "Disease Risk",      "Low",       "Monitor daily", GreenMid, Color(0xFFF9FBE7))
    )

    val recentInspections = listOf(
        RecentInspection("Today, 09:14 AM",     "Healthy",     "96%", GreenDark,         GreenLight),
        RecentInspection("Yesterday, 02:30 PM", "PLFD - Mild", "88%", Color(0xFFFF9800), OrangeLight),
        RecentInspection("Jun 7, 11:05 AM",     "Healthy",     "94%", GreenDark,         GreenLight)
    )

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBg)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ─────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenDark)
                    .padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 65.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (currentHour in 0..11) {
                            Image(
                                painterResource(R.drawable.morning),
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        } else if (currentHour in 12..17) {
                            Image(
                                painterResource(R.drawable.afternoon),
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        } else {
                            Image(
                                painterResource(R.drawable.evening),
                                contentDescription = null,
                                modifier = Modifier.size(13.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
                        }
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("$greeting,", color = Color.White,
                            fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (userName.isNotBlank()) "$userName!" else "Welcome!",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text("📍 Marilog District Plantation",
                        color = Color(0xFFA5D6A7), fontSize = 12.sp)
                }

                // ── Notification Bell ───────────────────────
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable {
                            showNotifPanel = true
                            notifViewModel?.markAllRead()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications",
                        tint = Color.White, modifier = Modifier.size(20.dp))

                    // Unread badge
                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEF5350)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                if (unreadCount > 9) "9+" else "$unreadCount",
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── Plantation Status Card ──────────────────────
            Box(modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-48).dp)
            ) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Plantation Status",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp, color = TextPrimary)
                            Surface(shape = RoundedCornerShape(50), color = GreenLight) {
                                Text("Low Risk", color = GreenMid,
                                    fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Thermostat, contentDescription = null,
                                    tint = OrangeDark, modifier = Modifier.size(20.dp))
                                Text("28°C", fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp, color = TextPrimary)
                                Text("Temperature", color = TextMuted, fontSize = 11.sp)
                            }
                            HorizontalDivider(modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .align(Alignment.CenterVertically),
                                color = BorderGray)
                            Column(modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null,
                                    tint = BlueDark, modifier = Modifier.size(20.dp))
                                Text("72%", fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp, color = TextPrimary)
                                Text("Humidity", color = TextMuted, fontSize = 11.sp)
                            }
                            HorizontalDivider(modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .align(Alignment.CenterVertically),
                                color = BorderGray)
                            Column(modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(Modifier.height(6.dp))
                                Box(modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(GreenMid))
                                Spacer(Modifier.height(4.dp))
                                Text("Good", fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp, color = TextPrimary)
                                Text("Conditions", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // ── Quick Actions ───────────────────────────────
            Column(modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-36).dp)
            ) {
                Text("Quick Actions", fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = Color(0xFF4A4A4A),
                    modifier = Modifier.padding(bottom = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quickActions.forEach { action ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(action.bgColor)
                                .clickable { onNavigate(action.route) }
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(action.iconColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(action.icon, contentDescription = action.label,
                                    tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(action.label, fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = action.iconColor, lineHeight = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Sensor Status ───────────────────────────
                Text("Sensor Status", fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, color = Color(0xFF4A4A4A),
                    modifier = Modifier.padding(bottom = 10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    statusCards.chunked(2).forEach { rowCards ->
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            rowCards.forEach { card -> SensorStatusCard(card) }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Recent Inspections ──────────────────────
                if (!isGuest) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Recent Inspections", fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp, color = Color(0xFF4A4A4A))
                        TextButton(onClick = { onNavigate("history") }) {
                            Text("View all", color = GreenDark,
                                fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Default.ChevronRight, contentDescription = null,
                                tint = GreenDark, modifier = Modifier.size(16.dp))
                        }
                    }

                    recentInspections.forEach { item ->
                        Spacer(Modifier.height(8.dp))
                        RecentInspectionRow(item, onClick = { onNavigate("history-detail") })
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
// ── Sensor Info Dialog ───────────────────────────────────
        if (showSensorDialog && selectedSensorNotif != null) {
            AlertDialog(
                onDismissRequest = { showSensorDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📡 ", fontSize = 18.sp)
                        Text(selectedSensorNotif!!.title,
                            fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(selectedSensorNotif!!.message,
                            fontSize = 13.sp, color = Color(0xFF424242),
                            lineHeight = 19.sp)
                        HorizontalDivider()
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Thermostat, contentDescription = null,
                                tint = OrangeDark, modifier = Modifier.size(14.dp))
                            Text("Temp: $tempText", fontSize = 12.sp, color = TextMuted)
                            Spacer(Modifier.width(8.dp))
                            Icon(Icons.Default.WaterDrop, contentDescription = null,
                                tint = BlueDark, modifier = Modifier.size(14.dp))
                            Text("Humidity: $humText", fontSize = 12.sp, color = TextMuted)
                        }
                        Text("Time: ${selectedSensorNotif!!.time}",
                            fontSize = 12.sp, color = TextMuted)
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showSensorDialog = false
                            showNotifPanel = false
                            onNavigate("ble-pairing")
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = GreenDark)
                    ) {
                        Text("Go to BLE Pairing", fontWeight = FontWeight.SemiBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSensorDialog = false }) {
                        Text("Close", color = Color.Gray)
                    }
                }
            )
        }
        // ── Notification Panel ───────────────────────────────
        if (showNotifPanel) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable { showNotifPanel = false }
            )
            Card(
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .align(Alignment.TopCenter)
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GreenDark)
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifications", color = Color.White,
                            fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        TextButton(onClick = {
                            notifViewModel?.clear()
                            showNotifPanel = false
                        }) {
                            Text("Clear all", color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp)
                        }
                    }

                    if (notifications.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("🔔", fontSize = 28.sp)
                                Spacer(Modifier.height(8.dp))
                                Text("No notifications yet",
                                    color = Color(0xFF9E9E9E), fontSize = 13.sp)
                            }
                        }
                    } else {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            notifications.forEach { notif ->
                                NotifPanelRow(
                                    notif = notif,
                                    onClick = {
                                        when (notif.type) {
                                            NotifType.SCAN, NotifType.DISEASE -> {
                                                showNotifPanel = false
                                                onNavigate("history-detail")
                                            }
                                            NotifType.SENSOR -> {
                                                selectedSensorNotif = notif
                                                showSensorDialog = true
                                            }
                                            NotifType.INFO -> {
                                                showNotifPanel = false
                                            }
                                        }
                                    }
                                )
                                HorizontalDivider(color = Color(0xFFF0F0F0))
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Bottom Navigation Bar ───────────────────────────────────
@Composable
fun AppBottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        BottomNavItem(Icons.Default.Home,                 "Home",    "home"),
        BottomNavItem(Icons.Default.CropFree,             "Scan",    "scan"),
        BottomNavItem(Icons.Default.History,              "History", "history"),
        BottomNavItem(Icons.AutoMirrored.Filled.MenuBook, "Guide",   "disease-guide"),
        BottomNavItem(Icons.Default.Person,               "Profile", "profile")
    )

    Surface(
        color = Color.White,
        shadowElevation = 12.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { item ->
                val isSelected = currentRoute == item.route
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            indication = null,
                            interactionSource = remember {
                                androidx.compose.foundation.interaction.MutableInteractionSource()
                            }
                        ) { if (!isSelected) onNavigate(item.route) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) GreenLight else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (isSelected) GreenDark else Color(0xFFB0B0B0),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) GreenDark else Color(0xFFB0B0B0)
                    )
                }
            }
        }
    }
}

// ── Sensor Status Card ──────────────────────────────────────
@Composable
fun SensorStatusCard(data: StatusCardData) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(data.bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(data.icon, contentDescription = null,
                        tint = data.valueColor, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(6.dp))
                Text(data.label, color = TextMuted,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    lineHeight = 13.sp)
            }
            Spacer(Modifier.height(8.dp))
            Text(data.value, fontWeight = FontWeight.Bold,
                fontSize = 16.sp, color = data.valueColor)
            Text(data.sub, color = TextMuted, fontSize = 10.sp)
        }
    }
}

// ── Recent Inspection Row ───────────────────────────────────
@Composable
fun RecentInspectionRow(item: RecentInspection, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(item.bg),
                contentAlignment = Alignment.Center
            ) {
                Text("🍃", fontSize = 18.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.result, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp, color = TextPrimary)
                Text(item.date, color = TextMuted, fontSize = 12.sp)
            }
            Surface(shape = RoundedCornerShape(50), color = item.bg) {
                Text(item.confidence, color = item.color,
                    fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
private fun NotifPanelRow(notif: AppNotification, onClick: () -> Unit) {
    val emoji = when (notif.type) {
        NotifType.DISEASE -> "⚠️"
        NotifType.SENSOR  -> "📡"
        NotifType.SCAN    -> "✅"
        NotifType.INFO    -> "ℹ️"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (!notif.isRead) Color(0xFFF1F8E9) else Color.White)
            .clickable { onClick() }                // ← add this
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(emoji, fontSize = 20.sp, modifier = Modifier.padding(top = 2.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(notif.title, fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp, color = Color(0xFF1C1C1C))
            Text(notif.message, fontSize = 12.sp,
                color = Color(0xFF616161), lineHeight = 17.sp)
            Spacer(Modifier.height(3.dp))
            Text("Tap to view details",                // ← add this
                fontSize = 10.sp, color = GreenDark,
                fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.width(8.dp))
        Text(notif.time, fontSize = 10.sp, color = Color(0xFF9E9E9E))
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}