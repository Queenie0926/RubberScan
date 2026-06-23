package com.example.rubberscan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Data model ─────────────────────────────────────────────
private data class SeverityLevel(
    val label: String,
    val range: String,
    val color: Color,
    val maxVal: Int
)

// ── Sample data ────────────────────────────────────────────
private val severityLevels = listOf(
    SeverityLevel("Mild",     "< 25%",    Color(0xFFF9A825), 25),
    SeverityLevel("Moderate", "25–50%",   Color(0xFFE65100), 50),
    SeverityLevel("Severe",   "> 50%",    Color(0xFFC62828), 100)
)

private const val severityValue = 38
private val currentLevel = when {
    severityValue < 25  -> severityLevels[0]
    severityValue <= 50 -> severityLevels[1]
    else                -> severityLevels[2]
}

// ── Severity Screen ────────────────────────────────────────
@Composable
fun SeverityScreen(onBack: () -> Unit = {}) {

    var startAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { startAnimation = true }

    val animatedSweep by animateFloatAsState(
        targetValue = if (startAnimation) severityValue / 100f else 0f,
        animationSpec = tween(durationMillis = 1000, delayMillis = 200),
        label = "gauge"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8F1))
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1B5E20))
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
            Text("Severity Assessment", color = Color.White,
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Gauge Card ───────────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Affected Leaf Area",
                        color = Color(0xFF757575),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium)

                    Spacer(Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Semicircle gauge
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val radius = minOf(w, h) * 0.42f
                            val cx = w / 2
                            val cy = h * 0.72f
                            val strokeW = 28.dp.toPx()

                            // Background arc (gray)
                            drawArc(
                                color = Color(0xFFF3F4F6),
                                startAngle = 180f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(cx - radius, cy - radius),
                                size = Size(radius * 2, radius * 2),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = strokeW,
                                    cap = StrokeCap.Round
                                )
                            )
                            // Foreground arc (colored)
                            drawArc(
                                color = currentLevel.color,
                                startAngle = 180f,
                                sweepAngle = 180f * animatedSweep,
                                useCenter = false,
                                topLeft = Offset(cx - radius, cy - radius),
                                size = Size(radius * 2, radius * 2),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(
                                    width = strokeW,
                                    cap = StrokeCap.Round
                                )
                            )
                        }

                        // Center text
                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("$severityValue%",
                                fontWeight = FontWeight.Black,
                                fontSize = 42.sp,
                                color = currentLevel.color)
                            Text(currentLevel.label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = currentLevel.color)
                        }
                    }
                }
            }

            // ── Severity Scale Card ───────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Severity Scale",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 10.dp))

                    severityLevels.forEach { level ->
                        val isActive = level.label == currentLevel.label
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isActive) level.color.copy(alpha = 0.08f)
                                    else Color(0xFFF8F8F8)
                                )
                                .then(
                                    if (isActive) Modifier.padding(1.dp) else Modifier
                                )
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(level.color)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(level.label,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = if (isActive) level.color else Color(0xFF374151))
                                Text("Affected area ${level.range}",
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 11.sp)
                            }
                            if (isActive) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = level.color
                                ) {
                                    Text("Current", color = Color.White,
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                        }
                    }
                }
            }

            // ── Leaf Visualization Card ───────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Affected Area Visualization",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF424242),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp))

                    Canvas(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        val w = size.width
                        val h = size.height
                        val cx = w * 0.5f
                        val cy = h * 0.5f

                        // Healthy leaf body
                        rotate(-8f, pivot = Offset(cx, cy)) {
                            drawOval(
                                color = Color(0xFFA5D6A7),
                                topLeft = Offset(cx - w * 0.425f, cy - h * 0.393f),
                                size = Size(w * 0.85f, h * 0.786f)
                            )
                        }
                        // Disease spots
                        drawCircle(Color(0xFFFF9800).copy(alpha = 0.7f),
                            radius = w * 0.11f, center = Offset(cx - w * 0.175f, cy - h * 0.071f))
                        drawCircle(Color(0xFFFF9800).copy(alpha = 0.6f),
                            radius = w * 0.09f, center = Offset(cx + w * 0.075f, cy + h * 0.143f))
                        drawCircle(Color(0xFFE65100).copy(alpha = 0.5f),
                            radius = w * 0.06f, center = Offset(cx - w * 0.06f, cy - h * 0.25f))
                        // Midrib
                        drawLine(Color(0xFF2E7D32),
                            Offset(w * 0.125f, cy + h * 0.014f),
                            Offset(w * 0.875f, cy - h * 0.014f),
                            strokeWidth = 2.dp.toPx())
                        // Veins
                        val veinColor = Color(0xFF2E7D32).copy(alpha = 0.5f)
                        val vw = 1.2.dp.toPx()
                        drawLine(veinColor, Offset(cx, h * 0.179f), Offset(w * 0.125f, cy + h * 0.014f), vw)
                        drawLine(veinColor, Offset(cx, h * 0.179f), Offset(w * 0.875f, cy - h * 0.014f), vw)
                        drawLine(veinColor, Offset(cx, h * 0.179f), Offset(cx, h * 0.821f), vw)
                    }

                    Spacer(Modifier.height(12.dp))

                    // Legend
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFA5D6A7)))
                            Spacer(Modifier.width(4.dp))
                            Text("Healthy (62%)", color = Color(0xFF546E54), fontSize = 11.sp)
                        }
                        Spacer(Modifier.width(20.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier
                                .size(10.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color(0xFFFF9800)))
                            Spacer(Modifier.width(4.dp))
                            Text("Affected (38%)", color = Color(0xFFE65100), fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}