package com.example.rubberscan

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.rubberscan.ui.theme.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ── Data model ─────────────────────────────────────────────
data class DiseaseResult(
    val name: String,
    val confidence: Int,
    val severity: String,
    val color: Color,
    val bg: Color,
    val badge: String,
    val badgeColor: Color,
    val badgeBg: Color,
    val icon: ImageVector,
    val desc: String
)

enum class ResultType { HEALTHY, PLFD, CLF, MILDEW }

// ── Sample results data ───────────────────────────────────
private fun getResultData(type: ResultType): DiseaseResult = when (type) {
    ResultType.HEALTHY -> DiseaseResult(
        name = "Healthy Leaf",
        confidence = 96,
        severity = "None",
        color = Color(0xFF1B5E20),
        bg = Color(0xFFE8F5E9),
        badge = "Healthy",
        badgeColor = Color(0xFF1B5E20),
        badgeBg = Color(0xFFC8E6C9),
        icon = Icons.Default.CheckCircle,
        desc = "No disease detected. The rubber leaf appears healthy with normal coloration and structure."
    )
    ResultType.PLFD -> DiseaseResult(
        name = "Pestalotiopsis Leaf Fall Disease",
        confidence = 88,
        severity = "Moderate",
        color = Color(0xFFE65100),
        bg = Color(0xFFFFF3E0),
        badge = "PLFD",
        badgeColor = Color(0xFFE65100),
        badgeBg = Color(0xFFFFE0B2),
        icon = Icons.Default.Warning,
        desc = "Leaf spots with gray-brown centers and dark margins. Premature leaf drop may occur."
    )
    ResultType.CLF -> DiseaseResult(
        name = "Corynespora Leaf Fall Disease",
        confidence = 82,
        severity = "Severe",
        color = Color(0xFFC62828),
        bg = Color(0xFFFFEBEE),
        badge = "CLF",
        badgeColor = Color(0xFFC62828),
        badgeBg = Color(0xFFFFCDD2),
        icon = Icons.Default.Error,
        desc = "Characteristic fish-bone pattern on leaves. Rapid defoliation possible. Immediate treatment required."
    )
    ResultType.MILDEW -> DiseaseResult(
        name = "Oidium Powdery Mildew",
        confidence = 91,
        severity = "Mild",
        color = Color(0xFFF9A825),
        bg = Color(0xFFFFFDE7),
        badge = "Mildew",
        badgeColor = Color(0xFFF9A825),
        badgeBg = Color(0xFFFFF9C4),
        icon = Icons.Default.Warning,
        desc = "White powdery coating on young leaves. Affects photosynthesis. Early intervention recommended."
    )
}

// ── Result Screen ──────────────────────────────────────────
@Composable
fun ResultScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    result: ResultType = ResultType.PLFD
) {
    val data = getResultData(result)
    val dateFormat = remember { SimpleDateFormat("MMMM d, yyyy", Locale.US) }
    val today = remember { dateFormat.format(Date()) }

    var animateConfidence by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animateConfidence = true }

    val confidenceProgress by animateFloatAsState(
        targetValue = if (animateConfidence) data.confidence / 100f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = 300),
        label = "confidence"
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
            Text("Detection Result", color = Color.White,
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Main Result Card ──────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Top accent border
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(data.color)
                    )

                    Column(modifier = Modifier.padding(20.dp)) {
                        // Icon + Badge + Title
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(data.bg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(data.icon, contentDescription = null,
                                    tint = data.color, modifier = Modifier.size(28.dp))
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Surface(shape = RoundedCornerShape(50), color = data.badgeBg) {
                                    Text(data.badge, color = data.badgeColor,
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(data.name, fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp, color = Color(0xFF1C1C1C),
                                    lineHeight = 20.sp)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Confidence bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Confidence Level", color = Color(0xFF757575), fontSize = 12.sp)
                            Text("${data.confidence}%", fontWeight = FontWeight.Bold,
                                fontSize = 13.sp, color = data.color)
                        }
                        Spacer(Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFFF0F0F0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(confidenceProgress)
                                    .clip(RoundedCornerShape(50))
                                    .background(data.color)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(data.desc, color = Color(0xFF666666),
                            fontSize = 13.sp, lineHeight = 20.sp)

                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFF0F0F0))
                        Spacer(Modifier.height(16.dp))

                        // Severity / Date row
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Severity", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                                Spacer(Modifier.height(4.dp))
                                Surface(shape = RoundedCornerShape(8.dp), color = data.bg) {
                                    Text(data.severity, color = data.color,
                                        fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .width(1.dp)
                                    .height(36.dp)
                                    .background(Color(0xFFF0F0F0))
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Detection Date", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(today, fontWeight = FontWeight.Medium,
                                    fontSize = 11.sp, color = Color(0xFF424242))
                            }
                        }
                    }
                }
            }

            // ── Leaf Image Card ───────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        ScannedLeafIcon(showSpots = result != ResultType.HEALTHY)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Scanned leaf", color = Color(0xFF757575), fontSize = 11.sp)
                        Text("Sample_leaf_001.jpg", fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp, color = Color(0xFF1C1C1C))
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Thermostat, contentDescription = null,
                                    tint = Color(0xFFE65100), modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("28.4°C", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.WaterDrop, contentDescription = null,
                                    tint = Color(0xFF0D47A1), modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(2.dp))
                                Text("72%", color = Color(0xFF9E9E9E), fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // ── Navigation Options ────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

                ResultNavRow(
                    icon = Icons.Default.Warning,
                    iconTint = Color(0xFFE65100),
                    iconBg = Color(0xFFFFF3E0),
                    label = "View Severity Assessment",
                    onClick = { onNavigate("severity") }
                )

                ResultNavRow(
                    icon = Icons.Default.Thermostat,
                    iconTint = Color(0xFF0D47A1),
                    iconBg = Color(0xFFE3F2FD),
                    label = "Environmental Risk",
                    onClick = { onNavigate("environmental-risk") }
                )

                // Primary CTA button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1B5E20))
                        .clickable { onNavigate("recommendation") }
                        .padding(vertical = 16.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("View Recommendations", color = Color.White,
                        fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Result Nav Row ─────────────────────────────────────────
@Composable
fun ResultNavRow(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null,
                        tint = iconTint, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(label, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = Color(0xFF1C1C1C))
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = Color(0xFF9CA3AF), modifier = Modifier.size(18.dp))
        }
    }
}

// ── Scanned Leaf Icon (Canvas) ────────────────────────────────
@Composable
fun ScannedLeafIcon(showSpots: Boolean) {
    Canvas(modifier = Modifier.size(64.dp)) {
        val w = size.width
        val h = size.height

        rotate(-15f, pivot = Offset(w * 0.5f, h * 0.5f)) {
            drawOval(
                color = Color(0xFF4CAF50),
                topLeft = Offset(w * 0.1f, h * 0.25f),
                size = Size(w * 0.8f, h * 0.5f)
            )
        }
        drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(w * 0.175f, h * 0.525f),
            end = Offset(w * 0.825f, h * 0.475f),
            strokeWidth = 1.5f
        )
        val veinColor = Color(0xFF2E7D32).copy(alpha = 0.6f)
        drawLine(veinColor, Offset(w * 0.5f, h * 0.275f), Offset(w * 0.175f, h * 0.525f), strokeWidth = 1f)
        drawLine(veinColor, Offset(w * 0.5f, h * 0.275f), Offset(w * 0.825f, h * 0.475f), strokeWidth = 1f)
        drawLine(veinColor, Offset(w * 0.5f, h * 0.275f), Offset(w * 0.5f, h * 0.725f), strokeWidth = 1f)

        if (showSpots) {
            drawCircle(Color(0xFFFF9800).copy(alpha = 0.7f), radius = w * 0.0625f,
                center = Offset(w * 0.35f, h * 0.475f))
            drawCircle(Color(0xFFFF9800).copy(alpha = 0.6f), radius = w * 0.05f,
                center = Offset(w * 0.65f, h * 0.525f))
            drawCircle(Color(0xFFE65100).copy(alpha = 0.5f), radius = w * 0.0375f,
                center = Offset(w * 0.55f, h * 0.375f))
        }
    }
}