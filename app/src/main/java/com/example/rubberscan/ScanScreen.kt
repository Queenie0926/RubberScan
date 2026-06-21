package com.example.rubberscan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Scan Screen ────────────────────────────────────────────
@Composable
fun ScanScreen(
    onBack: () -> Unit = {},
    onCapture: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Background gradient (camera preview simulation) ──
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1A3A1A),
                            Color(0xFF2D5A2D),
                            Color(0xFF1A3A1A)
                        )
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val cx = w * 0.5f
                val cy = h * 0.5f
                rotate(-10f, pivot = Offset(cx, cy)) {
                    drawOval(
                        color = Color(0xFF4CAF50).copy(alpha = 0.3f),
                        topLeft = Offset(cx - w * 0.36f, cy - h * 0.13f),
                        size = androidx.compose.ui.geometry.Size(w * 0.72f, h * 0.26f)
                    )
                }
                drawLine(
                    Color(0xFF2E7D32).copy(alpha = 0.3f),
                    Offset(cx - w * 0.19f, cy), Offset(cx + w * 0.19f, cy),
                    strokeWidth = 3f
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ─────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(22.dp))
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null,
                        tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Sensor Connected", color = Color.White,
                        fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = "Flash",
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }

            // ── Sensor Panel ──────────────────────────────────
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Thermostat, contentDescription = null,
                        tint = Color(0xFFFF9800), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("28.4°C", color = Color.White,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.width(1.dp).height(16.dp)
                        .background(Color.White.copy(alpha = 0.2f)))
                    Spacer(Modifier.width(12.dp))

                    Icon(Icons.Default.WaterDrop, contentDescription = null,
                        tint = Color(0xFF64B5F6), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("72% RH", color = Color.White,
                        fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

                    Spacer(Modifier.width(12.dp))
                    Box(modifier = Modifier.width(1.dp).height(16.dp)
                        .background(Color.White.copy(alpha = 0.2f)))
                    Spacer(Modifier.width(12.dp))

                    PulsingDot()
                    Spacer(Modifier.width(4.dp))
                    Text("Live", color = Color(0xFF4CAF50), fontSize = 11.sp)
                }
            }

            // ── Leaf Guide Frame ───────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(256.dp, 176.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Corner brackets + dashed leaf outline + scan line
                    LeafGuideOverlay()
                }

                Spacer(Modifier.height(24.dp))

                // Instruction box
                Column(
                    modifier = Modifier
                        .widthIn(max = 280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Place a rubber leaf inside the guide frame.",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Hold steady for best results",
                        color = Color(0xFFBDBDBD),
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Bottom Controls ─────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Gallery",
                        tint = Color.White, modifier = Modifier.size(22.dp))
                }

                // Capture button
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onCapture() },
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        // White ring border
                        Canvas(modifier = Modifier.matchParentSize()) {
                            drawCircle(
                                color = Color.White,
                                radius = size.minDimension / 2 - 2.dp.toPx(),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4.dp.toPx())
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                    }
                }

                // Spacer to balance layout
                Spacer(modifier = Modifier.size(48.dp))
            }
        }
    }
}

// ── Pulsing "Live" Dot ─────────────────────────────────────
@Composable
fun PulsingDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    Box(
        modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFF4CAF50).copy(alpha = alpha))
    )
}

// ── Leaf Guide Overlay (brackets + dashed outline + scan line) ─
@Composable
fun LeafGuideOverlay() {
    val infiniteTransition = rememberInfiniteTransition(label = "scanline")
    val scanPosition by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scanY"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val bracketLen = 28.dp.toPx()
        val strokeW = 3.dp.toPx()

        // Corner brackets
        // top-left
        drawLine(Color.White, Offset(0f, bracketLen), Offset(0f, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(0f, 0f), Offset(bracketLen, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)
        // top-right
        drawLine(Color.White, Offset(w - bracketLen, 0f), Offset(w, 0f), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(w, 0f), Offset(w, bracketLen), strokeWidth = strokeW, cap = StrokeCap.Round)
        // bottom-right
        drawLine(Color.White, Offset(w, h - bracketLen), Offset(w, h), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(w, h), Offset(w - bracketLen, h), strokeWidth = strokeW, cap = StrokeCap.Round)
        // bottom-left
        drawLine(Color.White, Offset(bracketLen, h), Offset(0f, h), strokeWidth = strokeW, cap = StrokeCap.Round)
        drawLine(Color.White, Offset(0f, h), Offset(0f, h - bracketLen), strokeWidth = strokeW, cap = StrokeCap.Round)

        // Dashed leaf outline ellipse
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
        drawOval(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(w * 0.12f, h * 0.12f),
            size = androidx.compose.ui.geometry.Size(w * 0.76f, h * 0.76f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.5.dp.toPx(),
                pathEffect = dashEffect
            )
        )
        // Crosshair lines
        drawLine(
            Color.White.copy(alpha = 0.3f),
            Offset(w * 0.05f, h * 0.5f), Offset(w * 0.95f, h * 0.5f),
            strokeWidth = 1.dp.toPx(), pathEffect = dashEffect
        )
        drawLine(
            Color.White.copy(alpha = 0.3f),
            Offset(w * 0.5f, h * 0.08f), Offset(w * 0.5f, h * 0.92f),
            strokeWidth = 1.dp.toPx(), pathEffect = dashEffect
        )

        // Animated scan line
        drawLine(
            color = Color(0xFF4CAF50).copy(alpha = 0.7f),
            start = Offset(w * 0.06f, h * scanPosition),
            end = Offset(w * 0.94f, h * scanPosition),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )
    }
}