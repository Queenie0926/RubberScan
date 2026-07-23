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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

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

enum class ResultType {
    HEALTHY,
    PLFD,
    ANTHRACNOSE,
    ALGAL,
    MILDEW,
    UNIDENTIFIED,
    NOT_RUBBER
}

// ── Sample result data ─────────────────────────────────────
private fun getResultData(type: ResultType): DiseaseResult {
    return when (type) {
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

        ResultType.ANTHRACNOSE -> DiseaseResult(
            name = "Anthracnose Leaf Spot",
            confidence = 85,
            severity = "Moderate",
            color = Color(0xFF00B7EB),
            bg = Color(0xFFEFEBE9),
            badge = "Anthracnose",
            badgeColor = Color(0xFF00B7EB),
            badgeBg = Color(0xFFE0F7FA),
            icon = Icons.Default.Warning,
            desc = "Brown sunken spots with yellow halos and tip dieback. Spreads in warm, humid conditions."
        )

        ResultType.ALGAL -> DiseaseResult(
            name = "Algal Leaf Spot",
            confidence = 87,
            severity = "Mild",
            color = Color(0xFF00FFCE),
            bg = Color(0xFFE0F7FA),
            badge = "Algal",
            badgeColor = Color(0xFF00838F),
            badgeBg = Color(0xFFB2EBF2),
            icon = Icons.Default.Warning,
            desc = "Circular raised velvety grey-green to rust-orange spots. Caused by a parasitic green alga in humid areas."
        )

        ResultType.MILDEW -> DiseaseResult(
            name = "Oidium Powdery Mildew",
            confidence = 91,
            severity = "Mild",
            color = Color(0xFFFE0056),
            bg = Color(0xFFFFFDE7),
            badge = "Mildew",
            badgeColor = Color(0xFFFE0056),
            badgeBg = Color(0xFFFFF9C4),
            icon = Icons.Default.Warning,
            desc = "White powdery coating on young leaves. It affects photosynthesis, so early intervention is recommended."
        )

        ResultType.UNIDENTIFIED -> DiseaseResult(
            name = "Unidentified",
            confidence = 0,
            severity = "—",
            color = Color(0xFF616161),
            bg = Color(0xFFF5F5F5),
            badge = "Unknown",
            badgeColor = Color(0xFF616161),
            badgeBg = Color(0xFFE0E0E0),
            icon = Icons.Default.HelpOutline,
            desc = "We could not confidently identify a disease. Retake a clear and well-lit photo of a single rubber leaf."
        )

        ResultType.NOT_RUBBER -> DiseaseResult(
            name = "Not a Rubber Leaf",
            confidence = 0,
            severity = "—",
            color = Color(0xFF8D6E63),
            bg = Color(0xFFF5F0EE),
            badge = "Invalid",
            badgeColor = Color(0xFF8D6E63),
            badgeBg = Color(0xFFE0D6D2),
            icon = Icons.Default.ImageNotSupported,
            desc = "This does not appear to be a rubber tree leaf. Point the camera at a single rubber leaf and scan again."
        )
    }
}

// ── Result Screen ──────────────────────────────────────────
@Composable
fun ResultScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    result: ResultType = ResultType.PLFD,
    temperature: Float? = 28.4f,
    humidity: Float? = 72f,
    riskLevel: RiskLevel = RiskLevel.MODERATE
) {
    val data = getResultData(result)

    val isUncertain =
        result == ResultType.UNIDENTIFIED ||
                result == ResultType.NOT_RUBBER

    val dateFormat = remember {
        SimpleDateFormat("MMMM d, yyyy", Locale.US)
    }

    val today = remember {
        dateFormat.format(Date())
    }

    val temperatureText =
        temperature?.let { "%.1f°C".format(it) } ?: "—"

    val humidityText =
        humidity?.let { "%.1f%%".format(it) } ?: "—"

    var animateConfidence by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(Unit) {
        animateConfidence = true
    }

    val confidenceProgress by animateFloatAsState(
        targetValue = if (animateConfidence) {
            data.confidence / 100f
        } else {
            0f
        },
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 300
        ),
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
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 40.dp,
                    bottom = 20.dp
                ),
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
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(12.dp))

            Text(
                text = "Detection Result",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Main Result Card ────────────────────────────
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(data.color)
                    )

                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(data.bg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = data.icon,
                                    contentDescription = data.name,
                                    tint = data.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = data.badgeBg
                                ) {
                                    Text(
                                        text = data.badge,
                                        color = data.badgeColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(
                                            horizontal = 8.dp,
                                            vertical = 3.dp
                                        )
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Text(
                                    text = data.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1C1C1C),
                                    lineHeight = 20.sp
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        if (!isUncertain) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Confidence Level",
                                    color = Color(0xFF757575),
                                    fontSize = 12.sp
                                )

                                Text(
                                    text = "${data.confidence}%",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 13.sp,
                                    color = data.color
                                )
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
                        }

                        Text(
                            text = data.desc,
                            color = Color(0xFF666666),
                            fontSize = 13.sp,
                            lineHeight = 20.sp
                        )

                        if (!isUncertain) {
                            Spacer(Modifier.height(16.dp))

                            HorizontalDivider(
                                color = Color(0xFFF0F0F0)
                            )

                            Spacer(Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Severity",
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 11.sp
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = data.bg
                                    ) {
                                        Text(
                                            text = data.severity,
                                            color = data.color,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(
                                                horizontal = 8.dp,
                                                vertical = 4.dp
                                            )
                                        )
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
                                    horizontalAlignment =
                                        Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Detection Date",
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 11.sp
                                    )

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = today,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 11.sp,
                                        color = Color(0xFF424242)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!isUncertain) {
                // ── Scanned Leaf Card ───────────────────────
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
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
                            ScannedLeafIcon(
                                showSpots = result != ResultType.HEALTHY
                            )
                        }

                        Spacer(Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Scanned leaf",
                                color = Color(0xFF757575),
                                fontSize = 11.sp
                            )

                            Text(
                                text = "Sample_leaf_001.jpg",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = Color(0xFF1C1C1C)
                            )

                            Spacer(Modifier.height(4.dp))

                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.temperature),
                                        contentDescription = "Temperature",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(14.dp)
                                    )

                                    Spacer(Modifier.width(2.dp))

                                    Text(
                                        text = temperatureText,
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 11.sp
                                    )
                                }

                                Row(
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    Image(
                                        painter = painterResource(R.drawable.humidity),
                                        contentDescription = "Humidity",
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(14.dp)
                                    )

                                    Spacer(Modifier.width(2.dp))

                                    Text(
                                        text = humidityText,
                                        color = Color(0xFF9E9E9E),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Disease Risk + Temperature + Humidity ───
                EnvironmentalRiskSummary(
                    riskLevel = riskLevel,
                    temperature = temperature,
                    humidity = humidity
                )

                // ── Navigation Options ──────────────────────
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ResultNavRow(
                        icon = Icons.Default.Warning,
                        iconTint = Color(0xFFE65100),
                        iconBg = Color(0xFFFFF3E0),
                        label = "View Severity Assessment",
                        onClick = {
                            onNavigate("severity")
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1B5E20))
                            .clickable {
                                onNavigate("recommendation")
                            }
                            .padding(
                                vertical = 16.dp,
                                horizontal = 20.dp
                            ),
                        horizontalArrangement =
                            Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View Recommendations",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else {
                // ── Scan Again Button ───────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1B5E20))
                        .clickable {
                            onNavigate("scan")
                        }
                        .padding(
                            vertical = 16.dp,
                            horizontal = 20.dp
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "Scan Again",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Result Navigation Row ──────────────────────────────────
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
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Text(
                    text = label,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color(0xFF1C1C1C)
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Scanned Leaf Icon ──────────────────────────────────────
@Composable
fun ScannedLeafIcon(
    showSpots: Boolean
) {
    Canvas(
        modifier = Modifier.size(64.dp)
    ) {
        val width = size.width
        val height = size.height

        rotate(
            degrees = -15f,
            pivot = Offset(
                width * 0.5f,
                height * 0.5f
            )
        ) {
            drawOval(
                color = Color(0xFF4CAF50),
                topLeft = Offset(
                    width * 0.1f,
                    height * 0.25f
                ),
                size = Size(
                    width * 0.8f,
                    height * 0.5f
                )
            )
        }

        drawLine(
            color = Color(0xFF2E7D32),
            start = Offset(
                width * 0.175f,
                height * 0.525f
            ),
            end = Offset(
                width * 0.825f,
                height * 0.475f
            ),
            strokeWidth = 1.5f
        )

        val veinColor =
            Color(0xFF2E7D32).copy(alpha = 0.6f)

        drawLine(
            color = veinColor,
            start = Offset(
                width * 0.5f,
                height * 0.275f
            ),
            end = Offset(
                width * 0.175f,
                height * 0.525f
            ),
            strokeWidth = 1f
        )

        drawLine(
            color = veinColor,
            start = Offset(
                width * 0.5f,
                height * 0.275f
            ),
            end = Offset(
                width * 0.825f,
                height * 0.475f
            ),
            strokeWidth = 1f
        )

        drawLine(
            color = veinColor,
            start = Offset(
                width * 0.5f,
                height * 0.275f
            ),
            end = Offset(
                width * 0.5f,
                height * 0.725f
            ),
            strokeWidth = 1f
        )

        if (showSpots) {
            drawCircle(
                color = Color(0xFFFF9800).copy(alpha = 0.7f),
                radius = width * 0.0625f,
                center = Offset(
                    width * 0.35f,
                    height * 0.475f
                )
            )

            drawCircle(
                color = Color(0xFFFF9800).copy(alpha = 0.6f),
                radius = width * 0.05f,
                center = Offset(
                    width * 0.65f,
                    height * 0.525f
                )
            )

            drawCircle(
                color = Color(0xFFE65100).copy(alpha = 0.5f),
                radius = width * 0.0375f,
                center = Offset(
                    width * 0.55f,
                    height * 0.375f
                )
            )
        }
    }
}