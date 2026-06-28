package com.example.rubberscan

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.rubberscan.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.drawscope.rotate
import kotlinx.coroutines.delay


// ── Step labels ────────────────────────────────────────────
private val analysisSteps = listOf(
    "Analyzing Leaf",
    "Evaluating Disease Symptoms",
    "Checking Environmental Conditions",
    "Generating Recommendation",
)

// ── Screen ─────────────────────────────────────────────────
@Composable
fun ProcessingScreen(onComplete: () -> Unit = {}) {

    // ── Progress state (0f–100f) ──────────────────────────
    var progress by remember { mutableFloatStateOf(0f) }
    val stepIndex by remember { derivedStateOf { (progress / 100f * analysisSteps.size).toInt().coerceIn(0, analysisSteps.size - 1) } }

    LaunchedEffect(Unit) {
        while (progress < 100f) {
            delay(60L)
            progress = (progress + 1.2f).coerceAtMost(100f)
        }
        delay(600L)
        onComplete()
    }

    // ── Pulse animation for centre circle ─────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "processing")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(750, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )

    // ── Ring animations (3 expanding rings) ───────────────
    val ringScales = (1..3).map { i ->
        infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset((i * 400))
            ), label = "ring$i"
        )
    }
    val ringAlphas = (1..3).map { i ->
        infiniteTransition.animateFloat(
            initialValue = 0.5f, targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
                initialStartOffset = StartOffset((i * 400))
            ), label = "ringAlpha$i"
        )
    }

    // ── Layout ─────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ── Leaf Preview ───────────────────────────────────
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(CardBg)
                .border(1.dp, Color(0xFFDCEEDC), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center
        ) {
            LeafSvgCanvas(modifier = Modifier.size(128.dp))
        }

        Spacer(Modifier.height(32.dp))

        // ── Scanning Animation ─────────────────────────────
        Box(
            modifier = Modifier.size(96.dp),
            contentAlignment = Alignment.Center
        ) {
            // Outer green tint ring background
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(GreenLight)
            )

            // Expanding ripple rings
            (0..2).forEach { i ->
                val ringSize = 44.dp + 20.dp * (i + 1)
                val scale by ringScales[i]
                val alpha by ringAlphas[i]
                Box(
                    modifier = Modifier
                        .size(ringSize)
                        .scale(scale)
                        .clip(CircleShape)
                        .border(2.dp, Color(0xFF81C784).copy(alpha = alpha), CircleShape)
                )
            }

            // Pulsing centre circle with scan icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(GreenDark),
                contentAlignment = Alignment.Center
            ) {
                ScanIconCanvas(modifier = Modifier.size(32.dp))
            }
        }

        Spacer(Modifier.height(24.dp))

        // ── Title ──────────────────────────────────────────
        Text(
            "AI Analysis",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1C1C1C)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Scanning your rubber leaf for disease indicators...",
            fontSize = 13.sp,
            color = TextMuted2,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        // ── Steps Card ─────────────────────────────────────
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                analysisSteps.forEachIndexed { i, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Step indicator circle
                        val circleColor = when {
                            i < stepIndex -> GreenAccent
                            i == stepIndex -> GreenDark
                            else -> Color(0xFFF0F0F0)
                        }
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(circleColor),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                i < stepIndex -> {
                                    // Checkmark via Canvas
                                    Canvas(modifier = Modifier.size(12.dp)) {
                                        val w = size.width
                                        val h = size.height
                                        drawPath(
                                            path = Path().apply {
                                                moveTo(w * 0.15f, h * 0.5f)
                                                lineTo(w * 0.4f, h * 0.75f)
                                                lineTo(w * 0.85f, h * 0.2f)
                                            },
                                            color = Color.White,
                                            style = Stroke(
                                                width = 2.dp.toPx(),
                                                cap = StrokeCap.Round
                                            )
                                        )
                                    }
                                }
                                i == stepIndex -> {
                                    // Pulsing white dot
                                    val dotScale by infiniteTransition.animateFloat(
                                        initialValue = 1f, targetValue = 1.4f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(800),
                                            repeatMode = RepeatMode.Reverse
                                        ), label = "dot$i"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .scale(dotScale)
                                            .clip(CircleShape)
                                            .background(Color.White)
                                    )
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFD1D5DB))
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        // Step label
                        Text(
                            text = step,
                            fontSize = 13.sp,
                            fontWeight = if (i == stepIndex) FontWeight.SemiBold else FontWeight.Normal,
                            color = when {
                                i < stepIndex -> GreenAccent
                                i == stepIndex -> GreenDark
                                else -> TextMuted2
                            }
                        )
                    }

                    if (i < analysisSteps.lastIndex) {
                        HorizontalDivider(color = Color(0xFFF8F8F8))
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // ── Progress Bar ────────────────────────────────────
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Processing", fontSize = 11.sp, color = TextMuted2)
                Text(
                    "${progress.toInt()}%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = GreenDark
                )
            }
            Spacer(Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFE0E0E0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(GreenAccent)
                )
            }
        }
    }
}

// ── Leaf SVG drawn via Canvas ──────────────────────────────
@Composable
private fun LeafSvgCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f

        // Leaf ellipse (rotated -15°): drawn as a filled oval scaled to fit
        rotate(-15f, pivot = androidx.compose.ui.geometry.Offset(cx, cy)) {
            drawOval(
                color = Color(0xFF4CAF50),
                topLeft = androidx.compose.ui.geometry.Offset(cx - w * 0.375f, cy - h * 0.25f),
                size = androidx.compose.ui.geometry.Size(w * 0.75f, h * 0.5f)
            )
        }

        // Mid-vein
        drawLine(
            color = Color(0xFF2E7D32),
            start = androidx.compose.ui.geometry.Offset(w * 0.2375f, h * 0.53f),
            end = androidx.compose.ui.geometry.Offset(w * 0.7625f, h * 0.47f),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Primary veins from tip
        val tipX = w * 0.5f; val tipY = h * 0.3125f
        val baseLeft = androidx.compose.ui.geometry.Offset(w * 0.2375f, h * 0.53f)
        val baseRight = androidx.compose.ui.geometry.Offset(w * 0.7625f, h * 0.47f)
        val baseDown = androidx.compose.ui.geometry.Offset(w * 0.5f, h * 0.72f)

        for ((start, end) in listOf(
            Pair(androidx.compose.ui.geometry.Offset(tipX, tipY), baseLeft),
            Pair(androidx.compose.ui.geometry.Offset(tipX, tipY), baseRight),
            Pair(androidx.compose.ui.geometry.Offset(tipX, tipY), baseDown)
        )) {
            drawLine(Color(0xFF2E7D32), start, end,
                strokeWidth = 1.2.dp.toPx(), cap = StrokeCap.Round,
                alpha = 0.6f)
        }

        // Scan overlay dashed rect — approximated with 4 dashed lines
        val dashColor = Color(0xFF4CAF50).copy(alpha = 0.5f)
        val dashWidth = 1.5.dp.toPx()
        val left   = w * 0.125f;  val top    = h * 0.25f
        val right  = w * 0.875f;  val bottom = h * 0.75f
        val dashOn = 5.dp.toPx(); val dashOff = 3.dp.toPx()
        val dashEffect = PathEffect.dashPathEffect(floatArrayOf(dashOn, dashOff))

        drawRect(
            color = dashColor,
            topLeft = androidx.compose.ui.geometry.Offset(left, top),
            size = androidx.compose.ui.geometry.Size(right - left, bottom - top),
            style = Stroke(width = dashWidth, pathEffect = dashEffect)
        )
    }
}

// ── Scan icon (crosshair) drawn via Canvas ─────────────────
@Composable
private fun ScanIconCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r  = size.minDimension * 0.25f
        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)

        // Outer circle
        drawCircle(color = Color.White, radius = r, style = stroke)
        // Inner dot
        drawCircle(color = Color.White, radius = r * 0.4f)
        // Cross arms
        val arm = r * 0.75f
        drawLine(Color.White, androidx.compose.ui.geometry.Offset(cx, cy - r - arm),
            androidx.compose.ui.geometry.Offset(cx, cy - r), 2.dp.toPx(), StrokeCap.Round)
        drawLine(Color.White, androidx.compose.ui.geometry.Offset(cx, cy + r),
            androidx.compose.ui.geometry.Offset(cx, cy + r + arm), 2.dp.toPx(), StrokeCap.Round)
        drawLine(Color.White, androidx.compose.ui.geometry.Offset(cx - r - arm, cy),
            androidx.compose.ui.geometry.Offset(cx - r, cy), 2.dp.toPx(), StrokeCap.Round)
        drawLine(Color.White, androidx.compose.ui.geometry.Offset(cx + r, cy),
            androidx.compose.ui.geometry.Offset(cx + r + arm, cy), 2.dp.toPx(), StrokeCap.Round)
    }
}