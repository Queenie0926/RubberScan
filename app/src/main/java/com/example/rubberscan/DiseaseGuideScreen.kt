package com.example.rubberscan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens ──────────────────────────────────────────
private val DiseaseGreenDark = Color(0xFF1B5E20)
private val DiseasePageBg    = Color(0xFFF1F8F1)
private val DiseaseCardBg    = Color(0xFFFFFFFF)
private val DiseaseTextMuted = Color(0xFF9E9E9E)

// ── Data model ─────────────────────────────────────────────
data class DiseaseInfo(
    val id: String,
    val name: String,
    val shortName: String,
    val color: Color,
    val bg: Color,
    val emoji: String,
    val severity: String,
    val prevalence: String,
    val symptoms: List<String>,
    val causes: String,
    val actions: List<String>
)

// ── Sample data ────────────────────────────────────────────
private val diseaseList = listOf(
    DiseaseInfo(
        id = "plfd",
        name = "Pestalotiopsis LFD",
        shortName = "PLFD",
        color = Color(0xFFE65100),
        bg = Color(0xFFFFF3E0),
        emoji = "🍂",
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
        )
    ),
    DiseaseInfo(
        id = "clf",
        name = "Corynespora LFD",
        shortName = "CLF",
        color = Color(0xFFC62828),
        bg = Color(0xFFFFEBEE),
        emoji = "🍁",
        severity = "Severe",
        prevalence = "Epidemic Risk",
        symptoms = listOf(
            "Fish-bone vein necrosis pattern",
            "Rapid leaf wilting and abscission",
            "Dark water-soaked lesions",
            "Stem cankers in severe cases"
        ),
        causes = "Caused by Corynespora cassiicola. Highly contagious. Can devastate entire blocks.",
        actions = listOf(
            "Quarantine affected zones",
            "Apply systemic fungicides",
            "Consult plantation technician immediately",
            "Report to DA"
        )
    ),
    DiseaseInfo(
        id = "mildew",
        name = "Oidium Powdery Mildew",
        shortName = "Mildew",
        color = Color(0xFFF9A825),
        bg = Color(0xFFFFFDE7),
        emoji = "🌫️",
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
        )
    ),
    DiseaseInfo(
        id = "healthy",
        name = "Healthy Leaf Reference",
        shortName = "Healthy",
        color = Color(0xFF1B5E20),
        bg = Color(0xFFE8F5E9),
        emoji = "🌿",
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
        )
    )
)

// ── Disease Guide Screen ───────────────────────────────────
@Composable
fun DiseaseGuideScreen(onBack: () -> Unit = {}) {
    var search by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<DiseaseInfo?>(null) }

    if (selected != null) {
        DiseaseDetailView(disease = selected!!, onBack = { selected = null })
        return
    }

    val filtered = diseaseList.filter {
        it.name.contains(search, ignoreCase = true) ||
                it.shortName.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DiseasePageBg)
    ) {
        // ── Header ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DiseaseGreenDark)
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
                Text("Disease Guide", color = Color.White,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.15f))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, contentDescription = null,
                        tint = Color(0xFFA5D6A7), modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)
                    ) {
                        if (search.isEmpty()) {
                            Text("Search diseases",
                                color = Color.LightGray, fontSize = 14.sp)
                        }
                        BasicTextField(
                            value = search,
                            onValueChange = { search = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.DarkGray, fontSize = 14.sp),
                            singleLine = true,
                            cursorBrush = androidx.compose.ui.graphics.SolidColor(Color.LightGray),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            filtered.forEach { disease ->
                DiseaseListItem(disease = disease, onClick = { selected = disease })
            }
        }
    }
}

// ── Disease List Item ───────────────────────────────────────
@Composable
fun DiseaseListItem(disease: DiseaseInfo, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DiseaseCardBg),
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
                Text(disease.emoji, fontSize = 24.sp)
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
                    Text(disease.prevalence, color = DiseaseTextMuted, fontSize = 11.sp)
                }
                Spacer(Modifier.height(3.dp))
                Text(disease.name, fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp, color = Color(0xFF1C1C1C))
                Text("Severity: ${disease.severity}",
                    color = DiseaseTextMuted, fontSize = 11.sp)
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
            .background(DiseasePageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(DiseaseGreenDark)
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
                Text("Disease Information", color = Color.White,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DiseaseCardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(disease.bg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(disease.emoji, fontSize = 30.sp)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Surface(shape = RoundedCornerShape(50), color = disease.color) {
                            Text(disease.shortName, color = Color.White,
                                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(disease.name, fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = Color(0xFF1C1C1C))
                        Text("Severity: ${disease.severity} · ${disease.prevalence}",
                            color = DiseaseTextMuted, fontSize = 11.sp)
                    }
                }
            }

            // Symptoms card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DiseaseCardBg),
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
                            Box(
                                modifier = Modifier
                                    .padding(top = 7.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(disease.color)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(symptom, color = Color(0xFF555555), fontSize = 13.sp)
                        }
                    }
                }
            }

            // Causes card
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

            // Actions card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = DiseaseCardBg),
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