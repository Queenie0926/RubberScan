package com.example.rubberscan

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.example.rubberscan.ui.theme.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.layout.PaddingValues
// ── Data model ─────────────────────────────────────────────
data class DiseaseInfo(
    val id: String,
    val name: String,
    val shortName: String,
    val color: Color,
    val bg: Color,
    val severity: String,
    val prevalence: String,
    val symptoms: List<String>,
    val causes: String,
    val actions: List<String>,
    val images: List<Int> = emptyList()
)

// ── Sample data ────────────────────────────────────────────
private val diseaseList = listOf(

    DiseaseInfo(
        id = "plfd",
        name = "Pestalotiopsis Leaf Fall Disease",
        shortName = "PLFD",
        color = Color(0xFFE65100),
        bg = Color(0xFFFFF3E0),
        severity = "Moderate–Severe",
        prevalence = "Common",
        symptoms = listOf(
            "Gray-brown leaf spots with dark margins",
            "Premature leaf yellowing",
            "Progressive leaf drop from lower canopy",
            "Spots enlarge under wet conditions"
        ),
        causes = "Fungal pathogen Pestalotiopsis sp. Spreads via wind and rain splash during high humidity.",
        actions = listOf(
            "Apply copper-based fungicide",
            "Remove fallen leaves immediately",
            "Avoid irrigation at night",
            "Monitor weekly"
        ),
        images = listOf(
            R.drawable.plfd_1,
            R.drawable.plfd_2,
            R.drawable.plfd_3
        )
    ),
    DiseaseInfo(
        id = "anthracnose",
        name = "Anthracnose Leaf Spot",
        shortName = "Anthracnose",
        color = Color(0xFF00B7EB),
        bg = Color(0xFFEFEBE9),
        severity = "Moderate–Severe",
        prevalence = "Common",
        symptoms = listOf(
            "Brown to dark sunken spots with yellow halos",
            "Leaf tip dieback and blackening",
            "Distortion of young expanding leaves",
            "Spots enlarge and merge in wet weather"
        ),
        causes = "Caused by Colletotrichum spp. Spreads via rain splash and wind during warm, humid conditions.",
        actions = listOf(
            "Apply copper- or mancozeb-based fungicide",
            "Prune and remove infected leaves",
            "Improve air circulation in canopy",
            "Avoid overhead watering"
        ),
        images = listOf(
            R.drawable.anthracnose_1,
            R.drawable.anthracnose_2,
            R.drawable.anthracnose_3
        )
    ),
    DiseaseInfo(
        id = "algal",
        name = "Algal Leaf Spot",
        shortName = "Algal",
        color = Color(0xFF00FFCE),
        bg = Color(0xFFE0F7FA),
        severity = "Mild–Moderate",
        prevalence = "Humid Areas",
        symptoms = listOf(
            "Circular raised velvety grey-green spots",
            "Spots turn rust-orange with age",
            "Crusty dark centers on older lesions",
            "Mostly on upper leaf surface"
        ),
        causes = "Caused by the parasitic green alga Cephaleuros virescens. Favored by warm, humid, stagnant-air conditions.",
        actions = listOf(
            "Apply copper-based fungicide",
            "Prune to improve air flow and light",
            "Reduce canopy humidity",
            "Remove heavily infected leaves"
        ),
        images = listOf(
            R.drawable.algal_1,
            R.drawable.algal_2,
            R.drawable.algal_3
        )
    ),
    DiseaseInfo(
        id = "mildew",
        name = "Oidium Powdery Mildew",
        shortName = "Mildew",
        color = Color(0xFFFE0056),
        bg = Color(0xFFFFFDE7),
        severity = "Mild–Moderate",
        prevalence = "Seasonal",
        symptoms = listOf(
            "White powdery coating on young leaves",
            "Leaf distortion and curling",
            "Stunted shoot growth",
            "Premature bud drop"
        ),
        causes = "Caused by Oidium heveae. Favored by dry weather with high humidity at night.",
        actions = listOf(
            "Apply sulfur-based fungicide",
            "Spray young foliage early morning",
            "Improve air circulation"
        ),
        images = listOf(
            R.drawable.powdery_1,
            R.drawable.powdery_2,
            R.drawable.powdery_3
        )
    ),
    DiseaseInfo(
        id = "healthy",
        name = "Healthy Leaf",
        shortName = "Healthy",
        color = Color(0xFF1B5E20),
        bg = Color(0xFFE8F5E9),
        severity = "None",
        prevalence = "Reference",
        symptoms = listOf(
            "Uniform dark green coloration",
            "Smooth, waxy leaf surface",
            "Clear white latex on cut stem",
            "No spots, lesions, or powdery deposits"
        ),
        causes = "N/A — Healthy leaf shows strong immunity and proper management.",
        actions = listOf(
            "Continue regular monitoring",
            "Maintain plantation hygiene",
            "Record baseline data"
        ),
        images = listOf(
            R.drawable.healthy_1,
            R.drawable.healthy_2,
            R.drawable.healthy_3
        )
    )
)

private fun severityDotColor(severity: String): Color = when {
    severity.contains("Severe", true)   -> Color(0xFFE53935) // red
    severity.contains("Moderate", true) -> Color(0xFFFB8C00) // orange
    severity.contains("Mild", true)     -> Color(0xFFFDD835) // yellow
    severity.equals("None", true)       -> Color(0xFF43A047) // green
    else                                -> Color(0xFF9E9E9E)
}

@Composable
private fun StatBlock(label: String, value: String, dotColor: Color) {
    Column {
        Text(label, color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(9.dp).clip(CircleShape).background(dotColor))
            Spacer(Modifier.width(6.dp))
            Text(value, color = Color(0xFF333333), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

// ── Disease Guide Screen ───────────────────────────────────
@Composable
fun DiseaseGuideScreen(
    onBack: () -> Unit = {}
) {
    var selected by remember {
        mutableStateOf<DiseaseInfo?>(null)
    }

    if (selected != null) {
        DiseaseDetailView(
            disease = selected!!,
            onBack = { selected = null }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Header ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 20.dp
                )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Color.White.copy(alpha = 0.15f)
                        )
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
                    text = "Disease Guide",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // ── Disease List ────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            diseaseList.forEach { disease ->
                DiseaseListItem(
                    disease = disease,
                    onClick = {
                        selected = disease
                    }
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}


// ── Disease List Item ───────────────────────────────────────
@Composable
fun DiseaseListItem(disease: DiseaseInfo, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(disease.bg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.leaf),
                    contentDescription = disease.shortName,
                    tint = disease.color,          // ← recolors per disease automatically
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(shape = RoundedCornerShape(50), color = disease.color) {
                        Text(disease.shortName, color = Color.White,
                            fontSize = 11.sp, fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp))
                    }
                    Spacer(Modifier.width(6.dp))
                    Text(disease.prevalence, color = TextMuted, fontSize = 11.sp)
                }
                Spacer(Modifier.height(3.dp))
                Text(disease.name, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = Color(0xFF1C1C1C))
                Text("Severity: ${disease.severity}",
                    color = TextMuted, fontSize = 11.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = Color(0xFF9CA3AF), modifier = Modifier.size(18.dp))
        }
    }
}

// ── Disease Detail View ─────────────────────────────────────
@Composable
fun DiseaseDetailView(disease: DiseaseInfo, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .padding(20.dp)
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
                    Icon(Icons.Default.ChevronLeft, "Back",
                        tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    disease.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Image carousel ──
            DiseaseImageCarousel(
                images = disease.images,
                accentColor = disease.color
            )

            // ── Severity + Prevalence ──
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    StatBlock("Severity", disease.severity, severityDotColor(disease.severity))
                    StatBlock("Prevalence", disease.prevalence, Color(0xFFFB8C00))
                }
            }

            // ── Symptoms ──
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Symptoms", fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 10.dp))
                    disease.symptoms.forEach { symptom ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = disease.color,
                                modifier = Modifier.size(16.dp).padding(top = 1.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(symptom, color = Color(0xFF555555), fontSize = 13.sp)
                        }
                    }
                }
            }

            // ── Causes ──
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = disease.bg),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Causes & Spread", fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = disease.color,
                        modifier = Modifier.padding(bottom = 8.dp))
                    Text(disease.causes, color = Color(0xFF555555),
                        fontSize = 13.sp, lineHeight = 19.sp)
                }
            }

            // ── Recommended Actions ──
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Recommended Actions", fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp, color = Color(0xFF424242),
                        modifier = Modifier.padding(bottom = 10.dp))
                    disease.actions.forEachIndexed { index, action ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(disease.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("${index + 1}", color = Color.White,
                                    fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.width(10.dp))
                            Text(action, color = Color(0xFF555555), fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}
@Composable
fun DiseaseImageCarousel(
    images: List<Int>,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    if (images.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { images.size })

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            // peek + gap gives the "more images beside" carousel feel
            contentPadding = PaddingValues(horizontal = if (images.size > 1) 20.dp else 0.dp),
            pageSpacing = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(3.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                Image(
                    painter = painterResource(images[page]),
                    contentDescription = "Reference image ${page + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (images.size > 1) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(images.size) { index ->
                    val selected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (selected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (selected) accentColor else Color(0xFFD0D0D0))
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Text("Swipe to view more images", color = TextMuted, fontSize = 12.sp)
        }
    }
}
