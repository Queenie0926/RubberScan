package com.example.rubberscan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens ──────────────────────────────────────────
private val DetailGreenDark  = Color(0xFF1B5E20)
private val DetailPageBg     = Color(0xFFF1F8F1)
private val DetailCardBg     = Color(0xFFFFFFFF)
private val DetailTextMuted  = Color(0xFF9E9E9E)

// ── History Detail Screen ───────────────────────────────────
@Composable
fun HistoryDetailScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DetailPageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DetailGreenDark)
                .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                Text("Inspection Details", color = Color.White,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Share, contentDescription = "Share",
                    tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Captured Image Card ──────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DetailCardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Leaf illustration placeholder
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(176.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFFC8E6C9), Color(0xFFA5D6A7))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        LeafIllustration()
                    }

                    // Info row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Surface(shape = RoundedCornerShape(50), color = Color(0xFFFF9800)) {
                                Text("PLFD", color = Color.White,
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Pestalotiopsis LFD", fontWeight = FontWeight.Bold,
                                fontSize = 16.sp, color = Color(0xFF1C1C1C))
                            Text("Confidence: 88%", color = DetailTextMuted, fontSize = 11.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF3E0)) {
                                Text("Mild", color = Color(0xFFE65100),
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Severity Grade", color = DetailTextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            // ── Environmental Data ───────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DetailCardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Environmental Data", fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Temperature
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF3E0))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Thermostat, contentDescription = null,
                                tint = Color(0xFFE65100), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("29.1°C", fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp, color = Color(0xFF1C1C1C))
                                Text("Temperature", color = DetailTextMuted, fontSize = 11.sp)
                            }
                        }
                        // Humidity
                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFE3F2FD))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null,
                                tint = Color(0xFF0D47A1), modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("74%", fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp, color = Color(0xFF1C1C1C))
                                Text("Humidity", color = DetailTextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // ── Inspection Info ──────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DetailCardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Inspection Info", fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 12.dp))

                    InfoRow(
                        icon = Icons.Default.CalendarMonth,
                        iconTint = DetailGreenDark,
                        label = "Date & Time",
                        value = "Jun 8, 2026 · 02:30 PM"
                    )
                    Spacer(Modifier.height(10.dp))
                    InfoRow(
                        icon = Icons.Default.LocationOn,
                        iconTint = Color(0xFFE65100),
                        label = "Location",
                        value = "Marilog District – Block B"
                    )
                }
            }

            // ── Recommendation ────────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recommendation Applied", fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(bottom = 8.dp))
                    Text(
                        "Apply copper-based fungicide. Remove fallen leaves. Schedule re-inspection in 7 days.",
                        color = Color(0xFF2E7D32), fontSize = 13.sp, lineHeight = 19.sp
                    )
                }
            }

            // ── Export Button ──────────────────────────────────
            Button(
                onClick = { },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Download, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Export PDF Report", color = Color.White,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Info Row ────────────────────────────────────────────────
@Composable
fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFAFAFA))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconTint,
                modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(label, color = DetailTextMuted, fontSize = 11.sp)
            Text(value, fontWeight = FontWeight.Medium,
                fontSize = 13.sp, color = Color(0xFF1C1C1C))
        }
    }
}

// ── Simple Leaf Illustration (Canvas) ──────────────────────
@Composable
fun LeafIllustration() {
    Canvas(modifier = Modifier.size(160.dp, 110.dp)) {
        val w = size.width
        val h = size.height

        // Leaf body (ellipse)
        drawOval(
            color = Color(0xFF4CAF50),
            topLeft = Offset(w * 0.07f, h * 0.18f),
            size = androidx.compose.ui.geometry.Size(w * 0.86f, h * 0.7f)
        )
        // Disease spots
        drawCircle(
            color = Color(0xFFFF9800).copy(alpha = 0.7f),
            radius = w * 0.09f,
            center = Offset(w * 0.33f, h * 0.42f)
        )
        drawCircle(
            color = Color(0xFFFF9800).copy(alpha = 0.6f),
            radius = w * 0.07f,
            center = Offset(w * 0.55f, h * 0.56f)
        )
        // Midrib
        drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(w * 0.1f, h * 0.5f),
            end = Offset(w * 0.9f, h * 0.48f),
            strokeWidth = 3f,
            cap = StrokeCap.Round
        )
        // Veins
        val veinAlpha = Color(0xFF2E7D32).copy(alpha = 0.5f)
        drawLine(veinAlpha, Offset(w * 0.5f, h * 0.1f), Offset(w * 0.1f, h * 0.5f), strokeWidth = 1.5f)
        drawLine(veinAlpha, Offset(w * 0.5f, h * 0.1f), Offset(w * 0.9f, h * 0.48f), strokeWidth = 1.5f)
        drawLine(veinAlpha, Offset(w * 0.5f, h * 0.1f), Offset(w * 0.5f, h * 0.88f), strokeWidth = 1.5f)
    }
}