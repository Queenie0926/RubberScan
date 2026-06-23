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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

// ── Bottom Nav Item model ───────────────────────────────────
private data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

// ── Colour tokens ──────────────────────────────────────────
private val GreenDark   = Color(0xFF1B5E20)
private val GreenLight  = Color(0xFFE8F5E9)
private val GreenMid    = Color(0xFF388E3C)
private val BlueDark    = Color(0xFF0D47A1)
private val BlueLight   = Color(0xFFE3F2FD)
private val PurpleDark  = Color(0xFF6A1B9A)
private val PurpleLight = Color(0xFFF3E5F5)
private val TealDark    = Color(0xFF00695C)
private val TealLight   = Color(0xFFE0F2F1)
private val OrangeDark  = Color(0xFFE65100)
private val OrangeLight = Color(0xFFFFF3E0)
private val PageBg      = Color(0xFFF1F8F1)
private val CardBg      = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1C1C1C)
private val TextMuted   = Color(0xFF9E9E9E)
private val BorderGray  = Color(0xFFF0F0F0)

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
fun HomeScreen(onNavigate: (String) -> Unit = {}) {

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val greeting = when (currentHour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    val quickActions = listOf(
        QuickAction(Icons.Default.Bluetooth,        "Pair Sensor",   BlueDark,   BlueLight,   "ble-pairing"),
        QuickAction(Icons.Default.DocumentScanner, "Scan Leaf",     GreenDark,  GreenLight,  "scan"),
        QuickAction(Icons.AutoMirrored.Filled.MenuBook,         "Disease Guide", OrangeDark, OrangeLight, "disease-guide"),
        QuickAction(Icons.Default.History,          "View History",  PurpleDark,   PurpleLight,   "history")


    )

    val statusCards = listOf(
        StatusCardData(Icons.Default.Wifi,       "Sensor Connection", "Connected", "ESP32-RubberSense", GreenDark,  GreenLight),
        StatusCardData(Icons.Default.Thermostat, "Temperature",       "28.4°C",   "Normal range",      OrangeDark, OrangeLight),
        StatusCardData(Icons.Default.WaterDrop,  "Humidity",          "72%",      "Moderate",          BlueDark,   BlueLight),
        StatusCardData(Icons.Default.Warning,    "Disease Risk",      "Low",      "Monitor daily",     GreenMid,   Color(0xFFF9FBE7))
    )

    val recentInspections = listOf(
        RecentInspection("Today, 09:14 AM",     "Healthy",     "96%", GreenDark,           GreenLight),
        RecentInspection("Yesterday, 02:30 PM", "PLFD - Mild", "88%", Color(0xFFFF9800),   OrangeLight),
        RecentInspection("Jun 7, 11:05 AM",     "Healthy",     "94%", GreenDark,           GreenLight)
    )

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
                    if(currentHour in 0..11) {
                        Image(
                            painterResource(R.drawable.morning),
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            colorFilter = ColorFilter.tint(Color.White)
                        )
                    } else if(currentHour in 12..17) {
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
                    Text("$greeting,", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text("Chaquella!", color = Color.White,
                    fontSize = 21.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text("📍 Marilog District Plantation",
                    color = Color(0xFFA5D6A7), fontSize = 12.sp)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications",
                    tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }

        // ── Plantation Status Card ──────────────────────────
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

        // ── Quick Actions ───────────────────────────────────
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

            // ── Sensor Status ───────────────────────────────
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

            // ── Recent Inspections ──────────────────────────
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

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Bottom Navigation Bar ───────────────────────────────────
@Composable
fun AppBottomNavBar(currentRoute: String, onNavigate: (String) -> Unit) {
    val items = listOf(
        BottomNavItem(Icons.Default.Home,            "Home",    "home"),
        BottomNavItem(Icons.Default.CropFree,        "Scan",    "scan"),
        BottomNavItem(Icons.Default.History,         "History", "history"),
        BottomNavItem(Icons.AutoMirrored.Filled.MenuBook, "Guide",   "disease-guide"),
        BottomNavItem(Icons.Default.Person,          "Profile", "profile")
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
                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
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
        modifier = Modifier.fillMaxWidth()
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
                    fontSize = 11.sp, fontWeight = FontWeight.Medium,
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

