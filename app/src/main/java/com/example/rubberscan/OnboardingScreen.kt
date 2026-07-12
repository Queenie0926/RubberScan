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
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
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
// Pages 0..onboardPages.size-1 are the info slides; the final
// "page" is the ownership question. Answering it swaps the whole
// screen for a tailored splash that auto-advances to onComplete().
@Composable
fun OnboardingScreen(onComplete: () -> Unit = {}) {
    var page by remember { mutableStateOf(0) }
    var ownershipAnswer by remember { mutableStateOf<Boolean?>(null) }

    val ownershipPageIndex = onboardPages.size
    val totalPages         = onboardPages.size + 1
    val isOwnershipPage    = page == ownershipPageIndex

    // ── Post-answer splash takeover ──────────────────────
    if (ownershipAnswer != null) {
        SplashTransitionScreen(
            title = if (ownershipAnswer == true)
                "Let's protect your plantation" else "No problem — explore first",
            subtitle = if (ownershipAnswer == true)
                "Sign up to track diseases, monitor conditions, and manage your rubber trees."
            else
                "Continue as a guest to try scanning and browse the disease guide.",
            onFinished = onComplete
        )
        return
    }

    fun next() {
        if (page < totalPages - 1) page++
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // ── Skip ────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            if (!isOwnershipPage) {
                Text(
                    "Skip",
                    color = Color(0xFF000000),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .clickable { page = ownershipPageIndex }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // ── Illustration / Ownership Icon ────────────────────
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(if (isOwnershipPage) Color(0xFFE8F5E9) else onboardPages[page].bgLight)
                .padding(horizontal = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isOwnershipPage) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Agriculture,
                        contentDescription = null,
                        tint = Color(0xFF1B5E20),
                        modifier = Modifier.size(60.dp)
                    )
                }
            } else {
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
        }

        // ── Text + Dots + Button(s) ───────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isOwnershipPage) 340.dp else 300.dp)
                .padding(horizontal = 32.dp, vertical = 32.dp)
        ) {
            if (isOwnershipPage) {
                Column {
                    Text(
                        "Do you own or manage\na rubber tree plantation?",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B1B1B),
                        lineHeight = 28.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "This helps us tailor RubberScan to how you'll use it.",
                        fontSize = 14.sp,
                        color = Color(0xFF757575),
                        lineHeight = 21.sp
                    )
                }
            } else {
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
            }

            Spacer(Modifier.height(24.dp))

            // ── Dot indicators ────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (index in 0 until totalPages) {
                    val isActive = index == page
                    val dotColor = if (!isActive) Color(0xFFD1D5DB)
                        else if (isOwnershipPage) Color(0xFF1B5E20)
                        else onboardPages[page].color
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (isActive) 24.dp else 8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(dotColor)
                            .clickable { page = index }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Buttons ────────────────────────────────────────
            if (isOwnershipPage) {
                Column {
                    Button(
                        onClick = { ownershipAnswer = true },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B5E20)),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null,
                            tint = Color.White, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Yes, I do", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { ownershipAnswer = false },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFBDBDBD)),
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null,
                            tint = Color(0xFF757575), modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("No, not yet", color = Color(0xFF424242), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            } else {
                Button(
                    onClick = { next() },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = onboardPages[page].color),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Next", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(6.dp))
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// ── Illustration 1: Scan ─────────────────────────────────────
@Composable
fun ScanIllustration() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
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

                // Capture button
                drawCircle(Color(0xFF1B5E20), radius = 12f * scale, center = Offset(110f * scale, 158f * scale))
                drawCircle(Color.White, radius = 8f * scale, center = Offset(110f * scale, 158f * scale))
            }

            Image(
                painter = painterResource(R.drawable.illustration_11),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.99f)
                    .aspectRatio(1f)
                    .align(Alignment.TopCenter)
                    .offset(x = 0.dp, y = (-20).dp)  // tweak y to move up/down, x to move left/right
                    .rotate(-15f)
            )
        }

        Card(
            modifier = Modifier
                .size(width = 100.dp, height = 120.dp)
                .offset(y = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(2.dp, Color(0xFF90CAF9))
        ) { }
    }
}

// ── Illustration 2: Sensor ───────────────────────────────────

@Composable
fun SensorIllustration() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.illustration_22),
                    contentDescription = "Sensor illustration",
                    modifier = Modifier
                        .fillMaxWidth(0.99f)
                        .aspectRatio(1f)
                        .align(Alignment.TopCenter)
                        .offset(y = 8.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Card(
                modifier = Modifier
                    .size(width = 100.dp, height = 120.dp)
                    .offset(y = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(2.dp, Color(0xFF90CAF9))
            ) { }
         }
        }
    }



// ── Illustration 3: Alert ────────────────────────────────────
@Composable
fun AlertIllustration() {
    Box(modifier = Modifier.fillMaxSize()) {

        // ── PNG Bell ──────────────────────────────────────────
        Image(
            painter = painterResource(id = R.drawable.illustration_3),
            contentDescription = "Alert bell",
            modifier = Modifier
                .fillMaxWidth(0.99f)
                .aspectRatio(1f)
                .align(Alignment.TopCenter)
                .offset(y = 8.dp),
            contentScale = ContentScale.Fit
        )

        Canvas(modifier = Modifier.fillMaxSize()) {
            val scale = size.width / 220f
            val cardOffset = 30f  // ← increase/decrease this to push cards further down or up

        // Recommendation card 1 (green)
            drawRoundRect(
                color = Color(0xFFE8F5E9),
                topLeft = Offset(30f * scale, (125f + cardOffset) * scale),
                size = Size(70f * scale, 30f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * scale),
                style = Stroke(width = 1.5f * scale)
            )
            drawCircle(Color(0xFF4CAF50), radius = 6f * scale, center = Offset(44f * scale, (140f + cardOffset) * scale))
            drawRoundRect(Color(0xFFA5D6A7), topLeft = Offset(54f * scale, (135f + cardOffset) * scale),
                size = Size(38f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))
            drawRoundRect(Color(0xFFC8E6C9), topLeft = Offset(54f * scale, (143f + cardOffset) * scale),
                size = Size(28f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))

        // Recommendation card 2 (orange)
            drawRoundRect(
                color = Color(0xFFFFF3E0),
                topLeft = Offset(120f * scale, (125f + cardOffset) * scale),
                size = Size(70f * scale, 30f * scale),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f * scale),
                style = Stroke(width = 1.5f * scale)
            )
            drawCircle(Color(0xFFFF9800), radius = 6f * scale, center = Offset(134f * scale, (140f + cardOffset) * scale))
            drawRoundRect(Color(0xFFFFCC80), topLeft = Offset(144f * scale, (135f + cardOffset) * scale),
                size = Size(38f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))
            drawRoundRect(Color(0xFFFFE0B2), topLeft = Offset(144f * scale, (143f + cardOffset) * scale),
                size = Size(28f * scale, 5f * scale), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.5f * scale))

        // Recommendation card 3 (red)
            drawRoundRect(
                color = Color(0xFFFFEBEE),
                topLeft = Offset(75f * scale, (163f + cardOffset) * scale),
                size = Size(70f * scale, 30f * scale),
                cornerRadius = CornerRadius(8f * scale),
                style = Stroke(width = 1.5f * scale)
            )
            drawCircle(Color(0xFFE53935), radius = 6f * scale, center = Offset(89f * scale, (178f + cardOffset) * scale))
            drawRoundRect(Color(0xFFEF9A9A), topLeft = Offset(99f * scale, (173f + cardOffset) * scale),
                size = Size(38f * scale, 5f * scale), cornerRadius = CornerRadius(2.5f * scale))
            drawRoundRect(Color(0xFFFFCDD2), topLeft = Offset(99f * scale, (181f + cardOffset) * scale),
                size = Size(28f * scale, 5f * scale), cornerRadius = CornerRadius(2.5f * scale))
        }
    }
}



