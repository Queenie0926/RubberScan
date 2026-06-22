package com.example.rubberscan

import androidx.compose.animation.core.*
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
private val GreenDark      = Color(0xFF1B5E20)
private val PageBg         = Color(0xFFF1F8F1)
private val CardBg         = Color(0xFFFFFFFF)
private val TextMuted      = Color(0xFF9E9E9E)
private val OrangeAccent   = Color(0xFFE65100)
private val OrangeBg       = Color(0xFFFFF3E0)

// ── Data models ────────────────────────────────────────────
private enum class RiskLevel { LOW, MODERATE, HIGH }

private data class RiskConfig(
    val label: String,
    val color: Color,
    val bg: Color,
    val percent: Float   // 0f–100f
)

private data class Condition(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val label: String,
    val value: String,
    val status: String,
    val statusColor: Color,
    val barPercent: Float
)

private data class ClimateRiskItem(
    val emoji: String,
    val factor: String,
    val risk: String
)

// ── Static data ────────────────────────────────────────────
private fun riskConfigFor(level: RiskLevel) = when (level) {
    RiskLevel.LOW      -> RiskConfig("Low Risk",      Color(0xFF1B5E20), Color(0xFFE8F5E9), 28f)
    RiskLevel.MODERATE -> RiskConfig("Moderate Risk", Color(0xFFE65100), Color(0xFFFFF3E0), 58f)
    RiskLevel.HIGH     -> RiskConfig("High Risk",     Color(0xFFC62828), Color(0xFFFFEBEE), 85f)
}

private val conditions = listOf(
    Condition(
        icon = Icons.Default.Thermostat,
        iconTint = Color(0xFFE65100),
        iconBg = Color(0xFFFFF3E0),
        label = "Temperature",
        value = "28.4°C",
        status = "Normal",
        statusColor = Color(0xFF1B5E20),
        barPercent = 57f
    ),
    Condition(
        icon = Icons.Default.WaterDrop,
        iconTint = Color(0xFF0D47A1),
        iconBg = Color(0xFFE3F2FD),
        label = "Humidity",
        value = "72%",
        status = "Elevated",
        statusColor = Color(0xFFE65100),
        barPercent = 72f
    ),
    Condition(
        icon = Icons.Default.Air,
        iconTint = Color(0xFF546E7A),
        iconBg = Color(0xFFECEFF1),
        label = "Airflow",
        value = "2.4 m/s",
        status = "Low",
        statusColor = Color(0xFFE65100),
        barPercent = 24f
    ),
    Condition(
        icon = Icons.Default.WbSunny,
        iconTint = Color(0xFFF9A825),
        iconBg = Color(0xFFFFFDE7),
        label = "UV Index",
        value = "6.2",
        status = "Moderate",
        statusColor = Color(0xFFE65100),
        barPercent = 62f
    )
)

private val climateRisks = listOf(
    ClimateRiskItem("💧", "High humidity",      "Favors fungal disease spread"),
    ClimateRiskItem("🌬️", "Low airflow",        "Reduces leaf drying time"),
    ClimateRiskItem("🌡️", "Temperature range",  "Within disease development range"),
)

// ── Screen ─────────────────────────────────────────────────
@Composable
fun EnvironmentalRiskScreen(onBack: () -> Unit = {}) {
    val riskLevel = RiskLevel.MODERATE
    val config = riskConfigFor(riskLevel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
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
                Icon(
                    Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                "Environmental Risk",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Risk Meter Card ──────────────────────────────
            RiskMeterCard(config = config)

            // ── Conditions Grid ──────────────────────────────
            ConditionsGrid(conditions = conditions)

            // ── Climate Risk Summary ─────────────────────────
            ClimateRiskSummaryCard(items = climateRisks)

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Risk Meter Card ────────────────────────────────────────
@Composable
private fun RiskMeterCard(config: RiskConfig) {
    // Animate the thumb position (0f–1f fraction of track width)
    val thumbFraction by animateFloatAsState(
        targetValue = config.percent / 100f,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "riskThumb"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Title + badge row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Disease Risk Level",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )
                Surface(
                    shape = RoundedCornerShape(50),
                    color = config.color
                ) {
                    Text(
                        config.label,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Track + thumb
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val trackWidthPx = constraints.maxWidth.toFloat()

                // Segmented track
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(50))
                ) {
                    Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFA5D6A7)))
                    Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFFF176)))
                    Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFFCC80)))
                    Box(Modifier.weight(1f).fillMaxHeight().background(Color(0xFFEF9A9A)))
                }

                // Animated thumb
                val thumbOffsetDp = with(androidx.compose.ui.platform.LocalDensity.current) {
                    (trackWidthPx * thumbFraction - 10.dp.toPx()).toDp()
                }
                Box(
                    modifier = Modifier
                        .offset(x = thumbOffsetDp, y = 0.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(config.color)
                        .then(
                            Modifier.clip(CircleShape)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                }
            }

            // Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Low",      color = TextMuted, fontSize = 11.sp)
                Text("Moderate", color = TextMuted, fontSize = 11.sp)
                Text("High",     color = TextMuted, fontSize = 11.sp)
            }
        }
    }
}

// ── Conditions Grid ────────────────────────────────────────
@Composable
private fun ConditionsGrid(conditions: List<Condition>) {
    // 2-column grid via chunked rows
    conditions.chunked(2).forEach { rowItems ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rowItems.forEach { condition ->
                ConditionCard(
                    condition = condition,
                    modifier = Modifier.weight(1f)
                )
            }
            // Fill empty slot if odd count
            if (rowItems.size < 2) Spacer(Modifier.weight(1f))
        }
    }
}

// ── Condition Card ─────────────────────────────────────────
@Composable
private fun ConditionCard(condition: Condition, modifier: Modifier = Modifier) {
    val animatedBar by animateFloatAsState(
        targetValue = condition.barPercent / 100f,
        animationSpec = tween(durationMillis = 700, delayMillis = 200),
        label = "bar_${condition.label}"
    )

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Icon + label
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(condition.iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        condition.icon,
                        contentDescription = null,
                        tint = condition.iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(condition.label, color = TextMuted, fontSize = 11.sp)
            }

            Spacer(Modifier.height(8.dp))

            // Value
            Text(
                condition.value,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1C1C1C)
            )

            // Status
            Text(
                condition.status,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = condition.statusColor
            )

            Spacer(Modifier.height(8.dp))

            // Progress bar track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFF0F0F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedBar)
                        .fillMaxHeight()
                        .background(condition.statusColor)
                )
            }
        }
    }
}

// ── Climate Risk Summary Card ──────────────────────────────
@Composable
private fun ClimateRiskSummaryCard(items: List<ClimateRiskItem>) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE0B2)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = OrangeAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "Climate Risk Summary",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1C1C1C)
                )
            }

            // Risk items
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFF8F0))
                            .padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(item.emoji, fontSize = 16.sp)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                item.factor,
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = Color(0xFF1C1C1C)
                            )
                            Text(
                                item.risk,
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }
                    }
                }
            }
        }
    }
}