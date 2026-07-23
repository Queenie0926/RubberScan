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
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.ui.draw.alpha
import com.example.rubberscan.db.entity.Plantation


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
fun HomeScreen(
    onNavigate     : (String) -> Unit = {},
    userName       : String = "",
    bleViewModel   : BleViewModel? = null,
    isGuest        : Boolean = false,
    notifViewModel : NotificationViewModel? = null,
    plantation     : Plantation? = null
) {

    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

    val greeting = when (currentHour) {
        in 0..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }

    val notifications by (notifViewModel?.notifications
        ?: kotlinx.coroutines.flow.MutableStateFlow<List<AppNotification>>(emptyList())).collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var showNotifPanel      by remember { mutableStateOf(false) }
    var showSensorDialog    by remember { mutableStateOf(false) }
    var selectedSensorNotif by remember { mutableStateOf<AppNotification?>(null) }

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

        // Temperature and Humidity intentionally omitted here — they're
        // already shown in the Plantation Status card above.

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

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Main scrollable content ──────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PageBg)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF14501A), GreenDark, Color(0xFF227A2B))
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = (-40).dp)
                        .size(150.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.06f))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-35).dp, y = 30.dp)
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                )

                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 65.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(painterResource(R.drawable.hand_wave),
                            contentDescription = null, modifier = Modifier.size(13.dp),
                            colorFilter = ColorFilter.tint(Color(0xFFA5D6A7)))
                        Spacer(modifier = Modifier.width(5.dp))
                        Text("$greeting,", color = Color(0xFFA5D6A7),
                            fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (userName.isNotBlank()) "$userName!" else "Welcome!",
                        color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Plantation shown as a chip instead of bare text
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                Icons.Default.LocationOn, contentDescription = null,
                                tint = Color.White, modifier = Modifier.size(13.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = plantation?.name ?: "Add your plantation",
                                color = Color.White, fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(end = 20.dp, top = 12.dp)
                        .size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable {
                                showNotifPanel = true
                                notifViewModel?.markAllRead()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    if (unreadCount > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 1.dp, y = 1.dp)
                                .size(17.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF44336)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                fontSize = 9.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 9.sp
                            )
                        }
                    }
                }
            }

            // Plantation status
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
                            Text("Plantation Status", fontWeight = FontWeight.Black,
                                fontSize = 15.sp, color = TextPrimary)
                            Surface(shape = RoundedCornerShape(50), color = GreenLight) {
                                Text("Low Risk", color = GreenMid, fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painter = painterResource(R.drawable.temperature),
                                    contentDescription = "Temperature", contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(24.dp))
                                Text(tempText, fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp, color = TextPrimary)
                                Text("Temperature", color = TextMuted, fontSize = 11.sp)
                            }
                            HorizontalDivider(modifier = Modifier.width(1.dp).height(48.dp)
                                .align(Alignment.CenterVertically), color = BorderGray)
                            Column(modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Image(painter = painterResource(R.drawable.humidity),
                                    contentDescription = "Humidity", contentScale = ContentScale.Fit,
                                    modifier = Modifier.size(24.dp))
                                Text(humText, fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp, color = TextPrimary)
                                Text("Humidity", color = TextMuted, fontSize = 11.sp)
                            }
                            HorizontalDivider(modifier = Modifier.width(1.dp).height(48.dp)
                                .align(Alignment.CenterVertically), color = BorderGray)
                            Column(modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally) {
                                Spacer(Modifier.height(4.dp))
                                Image(painter = painterResource(R.drawable.record),
                                    contentDescription = conditionText, contentScale = ContentScale.Fit,
                                    colorFilter = ColorFilter.tint(conditionColor),
                                    modifier = Modifier.size(16.dp))
                                Spacer(Modifier.height(4.dp))
                                Text(conditionText, fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp, color = conditionColor)
                                Text("Conditions", color = TextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            //Quick actions
            Column(modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-36).dp)
            ) {
                Text("Quick Actions", fontWeight = FontWeight.Black,
                    fontSize = 15.sp, color = Color(0xFF4A4A4A),
                    modifier = Modifier.padding(bottom = 10.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    quickActions.forEach { action ->
                        val actionInteraction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .pressScale(interactionSource = actionInteraction)
                                .clip(RoundedCornerShape(16.dp))
                                .background(action.bgColor)
                                .clickable(interactionSource = actionInteraction, indication = null) { onNavigate(action.route) }
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(12.dp))
                                .background(action.iconColor), contentAlignment = Alignment.Center) {
                                Image(painter = painterResource(action.iconRes),
                                    contentDescription = action.label, modifier = Modifier.size(22.dp),
                                    colorFilter = ColorFilter.tint(Color.White))
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(action.label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                                color = action.iconColor, lineHeight = 13.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                PlantationCard(
                    plantation = plantation,
                    onNavigate = { onNavigate("plantation") }
                )

                Spacer(Modifier.height(16.dp))

                Text("Sensor Status", fontWeight = FontWeight.Black,
                    fontSize = 15.sp, color = Color(0xFF4A4A4A),
                    modifier = Modifier.padding(bottom = 10.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    statusCards.forEach { card ->
                        Box(modifier = Modifier.weight(1f)) {
                            SensorStatusCard(card)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                if (!isGuest) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
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

                // Clearance for the floating nav bar overlaying the content
                Spacer(Modifier.height(110.dp))
            }
        } // ← end of main Column

        // ── Sensor Info Dialog ───────────────────────────
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
                        Text(selectedSensorNotif!!.message, fontSize = 13.sp,
                            color = Color(0xFF424242), lineHeight = 19.sp)
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
                    ) { Text("Go to BLE Pairing", fontWeight = FontWeight.SemiBold) }
                },
                dismissButton = {
                    TextButton(onClick = { showSensorDialog = false }) {
                        Text("Close", color = Color.Gray)
                    }
                }
            )
        }

        // ── Notification Panel ───────────────────────────
        if (showNotifPanel) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.20f))
                        .clickable { showNotifPanel = false }
                )
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 14.dp,
                            end = 14.dp,
                            top = 70.dp
                        )
                        .heightIn(
                            min = 150.dp,
                            max = 460.dp
                        )
                        .align(Alignment.TopCenter)
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(GreenDark)
                                .padding(horizontal = 20.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notifications",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            TextButton(
                                onClick = {
                                    notifViewModel?.clear()
                                    showNotifPanel = false
                                }
                            ) {
                                Text(
                                    text = "Clear all",
                                    color = Color.White.copy(alpha = 0.90f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        if (notifications.isEmpty()) {
                            Box(modifier = Modifier.fillMaxWidth().height(120.dp),
                                contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(R.drawable.no_notification),
                                        contentDescription = "No notifications",
                                        modifier = Modifier.size(48.dp),
                                        contentScale = ContentScale.Fit
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        text = "No notifications yet",
                                        color = Color(0xFF757575),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
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
                                                NotifType.TREATMENT -> {
                                                    showNotifPanel = false
                                                    // optionally navigate somewhere, e.g. back to recommendation screen
                                                    // onNavigate("recommendation")
                                                }
                                                NotifType.INFO -> showNotifPanel = false
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

    } // ← end of outer Box
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

    // Floating pill: transparent outer box provides the inset,
    // the inner Surface is the visible rounded bar.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Surface(
            color = Color.White,
            shape = RoundedCornerShape(28.dp),
            shadowElevation = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
}

@Composable
fun PlantationCard(plantation: Plantation?, onNavigate: () -> Unit) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interactionSource = interaction)
            .clickable(interactionSource = interaction, indication = null) { onNavigate() }
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // ── Card header row ───────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null,
                        tint = GreenDark, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (plantation != null) "My Plantation" else "Add your plantation",
                        color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        plantation?.name ?: "Register to start tracking",
                        fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary
                    )
                }
                Icon(
                    if (plantation != null) Icons.Default.Edit else Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF), modifier = Modifier.size(18.dp)
                )
            }

            // ── Location detail ───────────────────────────
            if (plantation != null) {
                Spacer(Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(Modifier.height(10.dp))

                if (plantation.address.isNotBlank()) {
                    PlantationDetailRow("Address", plantation.address)
                }
                PlantationDetailRow("Barangay", plantation.barangay)
                PlantationDetailRow("City / Municipality", plantation.city)
                PlantationDetailRow("Province", plantation.province)
                PlantationDetailRow("Region", plantation.region)


            }
        }
    }
}

@Composable
private fun PlantationDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            label,
            color = TextMuted, fontSize = 12.sp,
            modifier = Modifier.width(120.dp)
        )
        Text(
            value.ifBlank { "—" },
            color = TextPrimary, fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
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


@Composable
fun RecentInspectionRow(item: RecentInspection, onClick: () -> Unit) {
    val interaction = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .pressScale(interactionSource = interaction)
            .clickable(interactionSource = interaction, indication = null) { onClick() }
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

@Composable
fun NotifPanelRow(
    notif: AppNotification,
    onClick: () -> Unit
) {
    val iconRes = when {
        notif.type == NotifType.SCAN -> R.drawable.completed
        notif.type == NotifType.DISEASE -> R.drawable.completed
        notif.type == NotifType.SENSOR -> R.drawable.ble_status
        else -> R.drawable.no_notification
    }

    val iconTint = when {
        notif.type == NotifType.SENSOR &&
                notif.title.contains("Disconnected", ignoreCase = true) -> {
            Color(0xFFD32F2F)
        }

        notif.type == NotifType.SENSOR &&
                notif.title.contains("Connected", ignoreCase = true) -> {
            Color(0xFF2E7D32)
        }

        else -> GreenDark
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(
                    color = iconTint.copy(alpha = 0.12f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = notif.title,
                tint = iconTint,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = notif.title,
                color = Color(0xFF212121),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notif.message,
                color = Color(0xFF616161),
                fontSize = 14.sp,
                lineHeight = 19.sp
            )

            if (notif.time.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = notif.time,
                    color = Color(0xFF9E9E9E),
                    fontSize = 12.sp
                )
            }
        }
    }
}

