package com.example.rubberscan

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.draw.rotate

// ── Data model ─────────────────────────────────────────────
data class OnboardPage(
    val title: String,
    val desc: String,
    val color: Color,
    val bgLight: Color,
    val illustration: @Composable () -> Unit
)


// ── Pages ──────────────────────────────────────────────────
private val onboardPages = listOf(
    OnboardPage(
        title = "Scan Rubber Leaves",
        desc = "Use your camera to instantly detect diseases in rubber tree leaves with AI-powered analysis.",
        color = Color(0xFF1B5E20),
        bgLight = Color(0xFFE8F5E9),
        illustration = { ScanIllustration() }
    ),
    OnboardPage(
        title = "Monitor Environmental Conditions",
        desc = "Track temperature and humidity in real-time to assess disease risk in your plantation.",
        color = Color(0xFF0D47A1),
        bgLight = Color(0xFFE3F2FD),
        illustration = { SensorIllustration() }
    ),
    OnboardPage(
        title = "Receive Early Warnings & Recommendations",
        desc = "Get instant alerts about disease outbreaks and expert recommendations for your plantation.",
        color = Color(0xFFE65100),
        bgLight = Color(0xFFFFF3E0),
        illustration = { AlertIllustration() }
    )
)

// ── Onboarding Screen ───────────────────────────────────────
@Composable
fun OnboardingScreen(onComplete: () -> Unit = {}) {
    var page by remember { mutableStateOf(0) }
    val current = onboardPages[page]

    fun next() {
        if (page < onboardPages.size - 1) page++ else onComplete()
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // ── Skip ────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                "Skip",
                color = Color(0xFF000000),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .clickable { onComplete() }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }

        // ── Illustration ───────────────────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(current.bgLight)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    (fadeIn(tween(350)) + slideInHorizontally(tween(350)) { it / 3 }) togetherWith
                            (fadeOut(tween(350)) + slideOutHorizontally(tween(350)) { -it / 3 })
                },
                label = "illustration"
            ) { pageIndex ->
                Box(modifier = Modifier.size(224.dp)) {
                    onboardPages[pageIndex].illustration()
                }
            }
        }

        // ── Text + Dots + Button ─────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 32.dp, vertical = 32.dp)
        ) {
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    (fadeIn(tween(300)) + slideInHorizontally(tween(300)) { it / 4 }) togetherWith
                            (fadeOut(tween(300)) + slideOutHorizontally(tween(300)) { -it / 4 })
                },
                label = "text"
            ) { pageIndex ->
                val p = onboardPages[pageIndex]
                Column {
                    Text(
                        p.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = p.color,
                        lineHeight = 28.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        p.desc,
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        lineHeight = 21.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Dot indicators ────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                onboardPages.forEachIndexed { index, _ ->
                    val isActive = index == page
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isActive) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isActive) current.color else Color(0xFFD1D5DB))
                            .clickable { page = index }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Next Button ────────────────────────────────────
            Button(
                onClick = { next() },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = current.color),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    if (page < onboardPages.size - 1) "Next" else "Get Started",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ── Illustration 1: Scan ─────────────────────────────────────
@Composable
fun ScanIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scale = size.width / 220f

        // Phone outline
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(60f * scale, 15f * scale),
            size = Size(100f * scale, 170f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f * scale),
            style = Stroke(width = 2f * scale)
        )
        // Viewfinder
        drawRoundRect(
            color = Color(0xFFE8F5E9),
            topLeft = Offset(72f * scale, 35f * scale),
            size = Size(76f * scale, 100f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * scale)
        )
        // Leaf
        rotate(-15f, pivot = Offset(110f * scale, 82f * scale)) {
            drawOval(
                color = Color(0xFF4CAF50),
                topLeft = Offset(82f * scale, 64f * scale),
                size = Size(56f * scale, 36f * scale)
            )
        }
        drawLine(Color(0xFF2E7D32), Offset(110f * scale, 65f * scale), Offset(110f * scale, 100f * scale), strokeWidth = 1.5f * scale)
        // Corner brackets
        val bracketColor = Color(0xFF1B5E20)
        val bw = 2.5f * scale
        // top-left
        drawLine(bracketColor, Offset(75f * scale, 38f * scale), Offset(75f * scale, 48f * scale), bw, StrokeCap.Round)
        drawLine(bracketColor, Offset(75f * scale, 38f * scale), Offset(85f * scale, 38f * scale), bw, StrokeCap.Round)
        // top-right
        drawLine(bracketColor, Offset(145f * scale, 38f * scale), Offset(145f * scale, 48f * scale), bw, StrokeCap.Round)
        drawLine(bracketColor, Offset(145f * scale, 38f * scale), Offset(135f * scale, 38f * scale), bw, StrokeCap.Round)
        // bottom-left
        drawLine(bracketColor, Offset(75f * scale, 132f * scale), Offset(75f * scale, 122f * scale), bw, StrokeCap.Round)
        drawLine(bracketColor, Offset(75f * scale, 132f * scale), Offset(85f * scale, 132f * scale), bw, StrokeCap.Round)
        // bottom-right
        drawLine(bracketColor, Offset(145f * scale, 132f * scale), Offset(145f * scale, 122f * scale), bw, StrokeCap.Round)
        drawLine(bracketColor, Offset(145f * scale, 132f * scale), Offset(135f * scale, 132f * scale), bw, StrokeCap.Round)
        // Scan line
        drawLine(
            color = Color(0xFF4CAF50).copy(alpha = 0.8f),
            start = Offset(72f * scale, 82f * scale),
            end = Offset(148f * scale, 82f * scale),
            strokeWidth = 1.5f * scale
        )
        // Capture button
        drawCircle(Color(0xFF1B5E20), radius = 12f * scale, center = Offset(110f * scale, 158f * scale))
        drawCircle(Color.White, radius = 8f * scale, center = Offset(110f * scale, 158f * scale))
    }
}

// ── Illustration 2: Sensor ───────────────────────────────────

@Composable
fun Leaf(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF4CAF50)
) {
    Canvas(modifier = modifier) {

        // Leaf body
        drawOval(
            color = color,
            size = Size(size.width, size.height)
        )

        // Center vein
        drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(size.width / 2, size.height * 0.15f),
            end = Offset(size.width / 2, size.height * 0.85f),
            strokeWidth = 3f
        )
    }
}
@Composable
fun SensorIllustration() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        // Left Leaf
        Leaf(
            modifier = Modifier
                .offset(x = (-75).dp, y = 5.dp)
                .size(width = 45.dp, height = 80.dp)
                .rotate(-25f),
            color = Color(0xFF4CAF50)
        )

        // Right Leaf
        Leaf(
            modifier = Modifier
                .offset(x = 75.dp, y = 5.dp)
                .size(width = 38.dp, height = 70.dp)
                .rotate(20f),
            color = Color(0xFF66BB6A)
        )
        Box(
            contentAlignment = Alignment.Center
        ) {

            // Main Card
            Card(
                modifier = Modifier
                    .size(width = 130.dp, height = 180.dp)
                    .offset(y = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                border = BorderStroke(
                    2.dp,
                    Color(0xFF90CAF9)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Spacer(Modifier.height(24.dp))

                    // Temperature Card
                    // ...
                }
            }

            // Floating WiFi Icon
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp),
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = Color(0xFF66BB6A),
                    modifier = Modifier
                        .padding(10.dp)
                        .size(30.dp)
                )
            }
        }

                Spacer(Modifier.height(8.dp))
        Column(
            modifier = Modifier.offset(y = 25.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                modifier = Modifier
                    .width(95.dp)
                    .height(75.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE3F2FD)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "28°",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1565C0)
                    )

                    Text(
                        text = "TEMPERATURE",
                        fontSize = 8.sp,
                        color = Color(0xFF1976D2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { 0.68f },
                modifier = Modifier
                    .width(72.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFF2196F3),
                trackColor = Color(0xFFD6EAF8)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Humidity: 68%",
                fontSize = 10.sp,
                color = Color(0xFF546E7A)
            )
        }
        }
            }


// ── Illustration 3: Alert ────────────────────────────────────
@Composable
fun AlertIllustration() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val scale = size.width / 220f

        // Bell body (simplified path using arcs/rect)
        drawRoundRect(
            color = Color(0xFFFF9800),
            topLeft = Offset(70f * scale, 20f * scale),
            size = Size(80f * scale, 88f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(28f * scale, 28f * scale)
        )
        drawRoundRect(
            color = Color(0xFFE65100),
            topLeft = Offset(102f * scale, 108f * scale),
            size = Size(16f * scale, 10f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f * scale)
        )
        // Warning triangle
        val trianglePath = androidx.compose.ui.graphics.Path().apply {
            moveTo(110f * scale, 38f * scale)
            lineTo(130f * scale, 75f * scale)
            lineTo(90f * scale, 75f * scale)
            close()
        }
        drawPath(trianglePath, color = Color(0xFFFFF8E1))

        // Recommendation card 1
        drawRoundRect(
            color = Color(0xFFE8F5E9),
            topLeft = Offset(30f * scale, 125f * scale),
            size = Size(70f * scale, 30f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * scale),
            style = Stroke(width = 1.5f * scale)
        )
        drawCircle(Color(0xFF4CAF50), radius = 6f * scale, center = Offset(44f * scale, 140f * scale))
        drawRoundRect(Color(0xFFA5D6A7), topLeft = Offset(54f * scale, 135f * scale),
            size = Size(38f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))
        drawRoundRect(Color(0xFFC8E6C9), topLeft = Offset(54f * scale, 143f * scale),
            size = Size(28f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))

        // Recommendation card 2
        drawRoundRect(
            color = Color(0xFFFFF3E0),
            topLeft = Offset(120f * scale, 125f * scale),
            size = Size(70f * scale, 30f * scale),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * scale),
            style = Stroke(width = 1.5f * scale)
        )
        drawCircle(Color(0xFFFF9800), radius = 6f * scale, center = Offset(134f * scale, 140f * scale))
        drawRoundRect(Color(0xFFFFCC80), topLeft = Offset(144f * scale, 135f * scale),
            size = Size(38f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))
        drawRoundRect(Color(0xFFFFE0B2), topLeft = Offset(144f * scale, 143f * scale),
            size = Size(28f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))

        // Leaf accents
        rotate(20f, pivot = Offset(25f * scale, 105f * scale)) {
            drawOval(Color(0xFF4CAF50).copy(alpha = 0.4f), topLeft = Offset(15f * scale, 90f * scale), size = Size(20f * scale, 30f * scale))
        }
        rotate(-20f, pivot = Offset(195f * scale, 105f * scale)) {
            drawOval(Color(0xFF4CAF50).copy(alpha = 0.4f), topLeft = Offset(185f * scale, 90f * scale), size = Size(20f * scale, 30f * scale))
        }
    }

    // "!" overlay
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            "!",
            color = Color(0xFFE65100),
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.Center).offset(y = (-65).dp)
        )
    }
}