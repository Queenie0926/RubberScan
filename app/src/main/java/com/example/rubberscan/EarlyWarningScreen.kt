package com.example.rubberscan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas                          // ← ADD
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import com.example.rubberscan.ui.theme.*
import androidx.compose.ui.graphics.drawscope.Stroke               // ← ADD
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


// ── Data models ────────────────────────────────────────────
data class RiskFactor(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val factor: String,
    val value: String,
    val desc: String,
    val isHigh: Boolean
)

data class PreventiveAction(
    val emoji: String,
    val action: String,
    val freq: String
)

// ── Sample data ────────────────────────────────────────────
private val riskFactors = listOf(
    RiskFactor(
        icon = Icons.Default.WaterDrop,
        iconTint = Color(0xFF0D47A1),
        iconBg = Color(0xFFFFEBEE),
        factor = "High Humidity",
        value = "82%",
        desc = "Exceeds safe threshold of 75%",
        isHigh = true
    ),
    RiskFactor(
        icon = Icons.Default.Thermostat,
        iconTint = Color(0xFFE65100),
        iconBg = Color(0xFFFFF3E0),
        factor = "Temperature Range",
        value = "26–31°C",
        desc = "Optimal range for fungal growth",
        isHigh = false
    ),
    RiskFactor(
        icon = Icons.Default.Visibility,
        iconTint = Color(0xFF6A1B9A),
        iconBg = Color(0xFFFFEBEE),
        factor = "Recent Rain Events",
        value = "3 days",
        desc = "Consecutive wet days observed",
        isHigh = true
    )
)

private val preventiveActions = listOf(
    PreventiveAction("🌿", "Conduct Daily Leaf Inspections", "Every morning"),
    PreventiveAction("💨", "Improve Canopy Ventilation", "This week"),
    PreventiveAction("🧪", "Apply Preventive Fungicide", "As precaution"),
    PreventiveAction("📊", "Log Environmental Data", "Twice daily")
)

// ── Early Warning Screen ───────────────────────────────────
@Composable
fun EarlyWarningScreen(onBack: () -> Unit = {}) {

    val infiniteTransition = rememberInfiniteTransition(label = "warning")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

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
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
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
            Text("Early Warning", color = Color.White,
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Warning Banner ───────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                elevation = CardDefaults.cardElevation(0.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.5.dp, Color(0xFFFFD54F)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9A825))
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(22.dp)
                                .scale(pulseScale)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Early Warning Alert", color = Color.White,
                            fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Environmental conditions favor disease development. Monitor plantation regularly.",
                            color = Color(0xFF555555),
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )
                        Spacer(Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Leaf Status: Healthy",
                                color = Color(0xFF1B5E20),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f))
                            Text("Just now",
                                color = TextMuted,
                                fontSize = 11.sp)
                        }
                    }
                }
            }

            // ── Risk Factors ─────────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Risk Factors",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 8.dp))

                    riskFactors.forEachIndexed { index, factor ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = Color(0xFFF8F8F8),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        RiskFactorRow(factor = factor)
                    }
                }
            }

            // ── Preventive Actions ────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Suggested Preventive Actions",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        preventiveActions.forEach { action ->
                            PreventiveActionRow(action = action)
                        }
                    }
                }
            }

            // ── CTA Button ─────────────────────────────────────
            Button(
                onClick = { },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GreenDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Spa, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start Monitoring Session", color = Color.White,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Risk Factor Row ────────────────────────────────────────
@Composable
fun RiskFactorRow(factor: RiskFactor) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(factor.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(factor.icon, contentDescription = null,
                tint = factor.iconTint, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(factor.factor, fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp, color = Color(0xFF1C1C1C))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (factor.isHigh) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)
                ) {
                    Text(
                        factor.value,
                        color = if (factor.isHigh) Color(0xFFC62828) else Color(0xFFE65100),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Text(factor.desc, color = TextMuted, fontSize = 11.sp)
        }
    }
}

// ── Preventive Action Row ──────────────────────────────────
@Composable
fun PreventiveActionRow(action: PreventiveAction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFE8F5E9))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(action.emoji, fontSize = 20.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(action.action, fontWeight = FontWeight.Medium,
                fontSize = 13.sp, color = Color(0xFF1C1C1C))
            Text(action.freq, color = TextMuted, fontSize = 11.sp)
        }
        // ── FIX: removed wrapping Box; Canvas is now a direct composable
        //         with its own Modifier.size(), and DrawScope APIs resolve
        //         correctly inside the lambda.
        Canvas(modifier = Modifier.size(24.dp)) {
            drawCircle(
                color = Color(0xFF81C784),
                radius = size.minDimension / 2f,
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color(0xFF81C784),
                radius = size.minDimension * 0.2f
            )
        }
    }
}