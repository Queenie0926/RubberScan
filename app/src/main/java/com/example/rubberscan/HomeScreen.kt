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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import com.example.rubberscan.ui.theme.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import androidx.compose.ui.layout.ContentScale

// ── Bottom Nav Item model ───────────────────────────────────
private data class BottomNavItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)


// ── Data models ────────────────────────────────────────────
data class QuickAction(
    val iconRes: Int,
    val label: String,
    val iconColor: Color,
    val bgColor: Color,
    val route: String
)

data class StatusCardData(
    val iconRes: Int,
    val label: String,
    val value: String,
    val sub: String,
    val valueColor: Color,
    val bgColor: Color,
    val tintIcon: Boolean = false
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
fun HomeScreen(onNavigate: (String) -> Unit = {}, userName: String = "", bleViewModel: BleViewModel? = null, isGuest: Boolean = false) {

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val greeting = when (currentHour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    val quickActions = listOf(
        QuickAction(
            iconRes = R.drawable.bluetooth,
            label = "Pair Sensor",
            iconColor = BlueDark,
            bgColor = BlueLight,
            route = "ble-pairing"
        ),
        QuickAction(
            iconRes = R.drawable.scanner,
            label = "Scan Leaf",
            iconColor = GreenDark,
            bgColor = GreenLight,
            route = "scan"
        ),
        QuickAction(
            iconRes = R.drawable.info,
            label = "Disease Guide",
            iconColor = OrangeDark,
            bgColor = OrangeLight,
            route = "disease-guide"
        ),
        QuickAction(
            iconRes = R.drawable.history,
            label = "View History",
            iconColor = PurpleDark,
            bgColor = PurpleLight,
            route = "history"
        )
    )

    val bleState      by (bleViewModel?.bleState      ?: kotlinx.coroutines.flow.MutableStateFlow(BleState.IDLE)).collectAsState()
    val bleTemp       by (bleViewModel?.temperature   ?: kotlinx.coroutines.flow.MutableStateFlow<Float?>(null)).collectAsState()
    val bleHumidity   by (bleViewModel?.humidity      ?: kotlinx.coroutines.flow.MutableStateFlow<Float?>(null)).collectAsState()
    val bleName       by (bleViewModel?.connectedName ?: kotlinx.coroutines.flow.MutableStateFlow("")).collectAsState()

    val isConnected   = bleState == BleState.CONNECTED
    val tempText      = if (bleTemp != null) "%.1f°C".format(bleTemp) else "—"
    val humText       = if (bleHumidity != null) "%.1f%%".format(bleHumidity) else "—"
    val connSubtitle  = if (isConnected) bleName.ifBlank { "RubberSense" } else "Not paired"

    val isGoodCondition = true
    val conditionColor =
        if (isGoodCondition) Color(0xFF2E7D32)
        else Color(0xFFC62828)

    val conditionText =
        if (isGoodCondition) "Good"
        else "At Risk"

    val diseaseRiskText =
        if (isGoodCondition) "Low"
        else "High"

    val diseaseRiskColor =
        if (isGoodCondition) Color(0xFF2E7D32)
        else Color(0xFFC62828)

    val diseaseRiskBg =
        if (isGoodCondition) Color(0xFFE8F5E9)
        else Color(0xFFFFEBEE)

    val statusCards = listOf(
        StatusCardData(
            iconRes = R.drawable.bluetooth,
            label = "Sensor Connection",
            value = if (isConnected) "Connected" else "Disconnected",
            sub = connSubtitle,
            valueColor = if (isConnected) {
                Color(0xFF2E7D32)
            } else {
                Color(0xFFC62828)
            },
            bgColor = if (isConnected) {
                Color(0xFFE8F5E9)
            } else {
                Color(0xFFFFEBEE)
            },
            tintIcon = true
        ),

        StatusCardData(
            iconRes = R.drawable.temperature,
            label = "Temperature",
            value = tempText,
            sub = if (bleTemp != null) {
                "Normal range"
            } else {
                "Pair sensor first"
            },
            valueColor = OrangeDark,
            bgColor = OrangeLight
        ),

        StatusCardData(
            iconRes = R.drawable.humidity,
            label = "Humidity",
            value = humText,
            sub = if (bleHumidity != null) {
                "Moderate"
            } else {
                "Pair sensor first"
            },
            valueColor = BlueDark,
            bgColor = BlueLight
        ),

        StatusCardData(
            iconRes = R.drawable.warning,
            label = "Disease Risk",
            value = diseaseRiskText,
            sub = if (isGoodCondition) {
                "Monitor daily"
            } else {
                "Take action"
            },
            valueColor = diseaseRiskColor,
            bgColor = diseaseRiskBg,
            tintIcon = true
        )
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
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-48).dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Plantation Status",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = GreenLight
                        ) {
                            Text(
                                text = "Low Risk",
                                color = GreenMid,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.temperature),
                                contentDescription = "Temperature",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )

                            Text(
                                text = "28°C",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )

                            Text(
                                text = "Temperature",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .align(Alignment.CenterVertically),
                            color = BorderGray
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.humidity),
                                contentDescription = "Humidity",
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.size(24.dp)
                            )

                            Text(
                                text = "72%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = TextPrimary
                            )

                            Text(
                                text = "Humidity",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(48.dp)
                                .align(Alignment.CenterVertically),
                            color = BorderGray
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(R.drawable.record),
                                contentDescription = conditionText,
                                contentScale = ContentScale.Fit,
                                colorFilter = ColorFilter.tint(conditionColor),
                                modifier = Modifier.size(24.dp)
                            )

                            Text(
                                text = conditionText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = conditionColor
                            )

                            Text(
                                text = "Conditions",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
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
            Text("Quick Actions", fontWeight = FontWeight.Black,
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
                            Image(
                                painter = painterResource(action.iconRes),
                                contentDescription = action.label,
                                modifier = Modifier.size(22.dp),
                                colorFilter = ColorFilter.tint(Color.White)
                            )
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
            Text("Sensor Status", fontWeight = FontWeight.Black,
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
            if (!isGuest) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Recent Inspections", fontWeight = FontWeight.Black,
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

@Composable
fun SensorStatusCard(data: StatusCardData) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)  // ← fixed height, same for all cards
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
                    Image(
                        painter = painterResource(data.iconRes),
                        contentDescription = data.label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(24.dp),
                        colorFilter = if (data.tintIcon) {
                            ColorFilter.tint(data.valueColor)
                        } else {
                            null
                        }
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    data.label, color = TextMuted,
                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                    lineHeight = 13.sp
                )
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
                Icon(
                    painter = painterResource(R.drawable.leaf),
                    contentDescription = item.result,
                    tint = item.color,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.result, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp, color = TextPrimary)
                Text(item.date, color = TextMuted, fontSize = 12.sp)
            }
            Surface(shape = RoundedCornerShape(50), color = item.bg) {
                Text(item.confidence, color = item.color,
                    fontSize = 12.sp, fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}

