package com.example.rubberscan

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens ──────────────────────────────────────────
private val RecGreenDark  = Color(0xFF1B5E20)
private val RecPageBg     = Color(0xFFF1F8F1)
private val RecCardBg     = Color(0xFFFFFFFF)
private val RecStepsBg    = Color(0xFFF8F8F8)
private val RecTextMuted  = Color(0xFF9E9E9E)

// ── Data model ─────────────────────────────────────────────
data class Recommendation(
    val priority: String,
    val icon: ImageVector,
    val iconTint: Color,
    val title: String,
    val desc: String,
    val color: Color,
    val bg: Color,
    val steps: List<String>
)

// ── Sample data ────────────────────────────────────────────
private val recommendations = listOf(
    Recommendation(
        priority = "Immediate",
        icon = Icons.Default.Warning,
        iconTint = Color(0xFFC62828),
        title = "Schedule Immediate Treatment",
        desc = "Apply copper-based fungicide to affected areas. Use 2.5 g/L concentration. Treat early morning or late afternoon.",
        color = Color(0xFFC62828),
        bg = Color(0xFFFFEBEE),
        steps = listOf(
            "Identify all affected trees",
            "Mix copper oxychloride at 2.5g/L",
            "Spray to complete coverage",
            "Repeat after 7 days"
        )
    ),
    Recommendation(
        priority = "Soon",
        icon = Icons.Default.Spa,
        iconTint = Color(0xFF1B5E20),
        title = "Apply Preventive Measures",
        desc = "Clear fallen leaves from the plantation floor to reduce spore load and disease spread.",
        color = Color(0xFF1B5E20),
        bg = Color(0xFFE8F5E9),
        steps = listOf(
            "Collect and burn fallen leaves",
            "Prune overcrowded canopies",
            "Improve plantation drainage"
        )
    ),
    Recommendation(
        priority = "Weekly",
        icon = Icons.Default.CheckCircle,
        iconTint = Color(0xFF0D47A1),
        title = "Continue Monitoring",
        desc = "Conduct weekly leaf inspections. Log temperature and humidity readings with this app.",
        color = Color(0xFF0D47A1),
        bg = Color(0xFFE3F2FD),
        steps = listOf(
            "Scan 3 random trees weekly",
            "Record environmental data",
            "Track disease progression"
        )
    ),
    Recommendation(
        priority = "Optional",
        icon = Icons.Default.Person,
        iconTint = Color(0xFF6A1B9A),
        title = "Consult Agricultural Technician",
        desc = "If disease spreads beyond 50% of trees, contact a certified rubber plantation technician.",
        color = Color(0xFF6A1B9A),
        bg = Color(0xFFF3E5F5),
        steps = listOf(
            "Export this inspection report",
            "Contact local DA office",
            "Schedule farm visit"
        )
    )
)

// ── Recommendation Screen ──────────────────────────────────
@Composable
fun RecommendationScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RecPageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RecGreenDark)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp)
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
                Text("Recommendations", color = Color.White,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ── Recommendation Cards ─────────────────────────────
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                Text(
                    "Based on: Pestalotiopsis LFD – Moderate Severity",
                    color = Color.Black,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

            recommendations.forEach { rec ->
                RecommendationCard(rec = rec)
            }

            // ── CTA Button ──────────────────────────────────
            Button(
                onClick = { },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RecGreenDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.CalendarMonth, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Save & Schedule Treatment", color = Color.White,
                    fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ── Recommendation Card ────────────────────────────────────
@Composable
fun RecommendationCard(rec: Recommendation) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = RecCardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // ── Top section ──────────────────────────────────
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Icon box
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(rec.bg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(rec.icon, contentDescription = null,
                        tint = rec.iconTint, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    // Priority badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = rec.color
                    ) {
                        Text(rec.priority, color = Color.White,
                            fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(rec.title, fontWeight = FontWeight.Bold,
                        fontSize = 14.sp, color = Color(0xFF1C1C1C))
                    Spacer(Modifier.height(4.dp))
                    Text(rec.desc, color = Color(0xFF757575),
                        fontSize = 12.sp, lineHeight = 18.sp)
                }
            }

            // ── Steps section ─────────────────────────────────
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(RecStepsBg)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rec.steps.forEachIndexed { index, step ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(rec.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", color = Color.White,
                                fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(10.dp))
                        Text(step, color = Color(0xFF555555), fontSize = 12.sp)
                    }
                }
            }
        }
    }
}