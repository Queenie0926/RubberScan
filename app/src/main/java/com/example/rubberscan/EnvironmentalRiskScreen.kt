package com.example.rubberscan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rubberscan.ui.theme.CardBg
import com.example.rubberscan.ui.theme.GreenDark
import com.example.rubberscan.ui.theme.PageBg
import com.example.rubberscan.ui.theme.TextMuted
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

// RiskLevel is expected to be defined in DiseaseRisk.kt:
//
// enum class RiskLevel {
//     LOW,
//     MODERATE,
//     HIGH,
//     UNKNOWN
// }

// ── Risk configuration ─────────────────────────────────────
private data class RiskConfig(
    val label: String,
    val color: Color,
    val bg: Color,
    val percent: Float
)

// ── Temperature/Humidity data ──────────────────────────────
private data class EnvironmentalCondition(
    val iconRes: Int,
    val iconBg: Color,
    val label: String,
    val value: String,
    val status: String,
    val statusColor: Color,
    val barPercent: Float
)

// ── Risk configuration mapper ──────────────────────────────
private fun riskConfigFor(
    level: RiskLevel
): RiskConfig {
    return when (level) {
        RiskLevel.LOW -> RiskConfig(
            label = "Low Risk",
            color = Color(0xFF1B5E20),
            bg = Color(0xFFE8F5E9),
            percent = 28f
        )

        RiskLevel.MODERATE -> RiskConfig(
            label = "Moderate Risk",
            color = Color(0xFFE65100),
            bg = Color(0xFFFFF3E0),
            percent = 58f
        )

        RiskLevel.HIGH -> RiskConfig(
            label = "High Risk",
            color = Color(0xFFC62828),
            bg = Color(0xFFFFEBEE),
            percent = 85f
        )

        RiskLevel.UNKNOWN -> RiskConfig(
            label = "No Data Yet",
            color = Color(0xFF757575),
            bg = Color(0xFFF5F5F5),
            percent = 0f
        )
    }
}

// ── Determine temperature status ───────────────────────────
private fun temperatureCondition(
    temperature: Float?
): EnvironmentalCondition {
    if (temperature == null) {
        return EnvironmentalCondition(
            iconRes = R.drawable.temperature,
            iconBg = Color(0xFFF5F5F5),
            label = "Temperature",
            value = "—",
            status = "No sensor data",
            statusColor = Color(0xFF757575),
            barPercent = 0f
        )
    }

    val isNormal = temperature in 23f..30f

    return EnvironmentalCondition(
        iconRes = R.drawable.temperature,
        iconBg = Color(0xFFFFF3E0),
        label = "Temperature",
        value = "%.1f°C".format(temperature),
        status = if (isNormal) "Normal" else "At Risk",
        statusColor = if (isNormal) {
            Color(0xFF1B5E20)
        } else {
            Color(0xFFC62828)
        },
        barPercent = ((temperature / 40f) * 100f)
            .coerceIn(0f, 100f)
    )
}

// ── Determine humidity status ──────────────────────────────
private fun humidityCondition(
    humidity: Float?
): EnvironmentalCondition {
    if (humidity == null) {
        return EnvironmentalCondition(
            iconRes = R.drawable.humidity,
            iconBg = Color(0xFFF5F5F5),
            label = "Humidity",
            value = "—",
            status = "No sensor data",
            statusColor = Color(0xFF757575),
            barPercent = 0f
        )
    }

    val isNormal = humidity in 60f..80f

    return EnvironmentalCondition(
        iconRes = R.drawable.humidity,
        iconBg = Color(0xFFE3F2FD),
        label = "Humidity",
        value = "%.1f%%".format(humidity),
        status = if (isNormal) "Normal" else "At Risk",
        statusColor = if (isNormal) {
            Color(0xFF1B5E20)
        } else {
            Color(0xFFC62828)
        },
        barPercent = humidity.coerceIn(0f, 100f)
    )
}

// ── Reusable risk summary ──────────────────────────────────
@Composable
fun EnvironmentalRiskSummary(
    riskLevel: RiskLevel = RiskLevel.MODERATE,
    temperature: Float? = 28.4f,
    humidity: Float? = 72f
) {
    val config = riskConfigFor(riskLevel)

    val conditions = listOf(
        temperatureCondition(temperature),
        humidityCondition(humidity)
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        RiskMeterCard(
            config = config
        )

        ConditionsGrid(
            conditions = conditions
        )
    }
}

// ── Full Environmental Risk Screen ─────────────────────────
@Composable
fun EnvironmentalRiskScreen(
    onBack: () -> Unit = {},
    riskLevel: RiskLevel = RiskLevel.MODERATE,
    temperature: Float? = 28.4f,
    humidity: Float? = 72f
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .statusBarsPadding()
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable {
                        onBack()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = "Environmental Risk",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            EnvironmentalRiskSummary(
                riskLevel = riskLevel,
                temperature = temperature,
                humidity = humidity
            )

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Disease Risk Meter ─────────────────────────────────────
@Composable
private fun RiskMeterCard(
    config: RiskConfig
) {
    val thumbFraction by animateFloatAsState(
        targetValue = config.percent / 100f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 300
        ),
        label = "riskThumb"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Disease Risk Level",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = Color(0xFF424242)
                )

                Surface(
                    shape = RoundedCornerShape(50),
                    color = config.bg
                ) {
                    Text(
                        text = config.label,
                        color = config.color,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 4.dp
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            BoxWithConstraints(
                modifier = Modifier.fillMaxWidth()
            ) {
                val trackWidthPx =
                    constraints.maxWidth.toFloat()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(50))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFA5D6A7))
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFFFF176))
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFFFCC80))
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color(0xFFEF9A9A))
                    )
                }

                val density = LocalDensity.current

                val thumbOffsetDp = with(density) {
                    (
                            trackWidthPx * thumbFraction -
                                    10.dp.toPx()
                            ).toDp()
                }

                Box(
                    modifier = Modifier
                        .offset(
                            x = thumbOffsetDp,
                            y = 0.dp
                        )
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(config.color)
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Low",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Text(
                    text = "Moderate",
                    color = TextMuted,
                    fontSize = 11.sp
                )

                Text(
                    text = "High",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// ── Temperature and Humidity Grid ──────────────────────────
@Composable
private fun ConditionsGrid(
    conditions: List<EnvironmentalCondition>
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        conditions.forEach { condition ->
            ConditionCard(
                condition = condition,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

// ── Temperature/Humidity Card ──────────────────────────────
@Composable
private fun ConditionCard(
    condition: EnvironmentalCondition,
    modifier: Modifier = Modifier
) {
    val animatedBar by animateFloatAsState(
        targetValue = condition.barPercent / 100f,
        animationSpec = tween(
            durationMillis = 700,
            delayMillis = 200
        ),
        label = "bar_${condition.label}"
    )

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBg
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
            .height(160.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(condition.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(condition.iconRes),
                    contentDescription = condition.label,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(Modifier.height(6.dp))

            // Label
            Text(
                text = condition.label,
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(Modifier.height(4.dp))

            // Main value
            Text(
                text = condition.value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = Color(0xFF1C1C1C),
                maxLines = 1
            )

            // Status
            Text(
                text = condition.status,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = condition.statusColor,
                maxLines = 1
            )

            Spacer(Modifier.weight(1f))

            // Progress bar
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
                        .clip(RoundedCornerShape(50))
                        .background(condition.statusColor)
                )
            }
        }
    }
}
