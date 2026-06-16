package com.example.rubberscan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
private val HistoryGreenDark  = Color(0xFF1B5E20)
private val HistoryGreenLight = Color(0xFFE8F5E9)
private val HistoryPageBg     = Color(0xFFF1F8F1)
private val HistoryCardBg     = Color(0xFFFFFFFF)
private val HistoryBorderGray = Color(0xFFF0F0F0)
private val HistoryTextMuted  = Color(0xFF9E9E9E)

// ── Data model ─────────────────────────────────────────────
data class InspectionRecord(
    val id: Int,
    val date: String,
    val time: String,
    val result: String,
    val severity: String,
    val temp: String,
    val humidity: String,
    val color: Color,
    val bg: Color,
    val emoji: String
)

// ── Sample data ────────────────────────────────────────────
private val sampleRecords = listOf(
    InspectionRecord(1, "Jun 9, 2026",  "09:14 AM", "Healthy", "None",     "28.2°C", "70%", Color(0xFF1B5E20), Color(0xFFE8F5E9), "🌿"),
    InspectionRecord(2, "Jun 8, 2026",  "02:30 PM", "PLFD",    "Mild",     "29.1°C", "74%", Color(0xFFF9A825), Color(0xFFFFFDE7), "🍂"),
    InspectionRecord(3, "Jun 7, 2026",  "11:05 AM", "Healthy", "None",     "27.8°C", "68%", Color(0xFF1B5E20), Color(0xFFE8F5E9), "🌿"),
    InspectionRecord(4, "Jun 6, 2026",  "08:50 AM", "CLF",     "Moderate", "30.3°C", "82%", Color(0xFFE65100), Color(0xFFFFF3E0), "🍁"),
    InspectionRecord(5, "Jun 5, 2026",  "03:15 PM", "Mildew",  "Mild",     "28.7°C", "65%", Color(0xFFF9A825), Color(0xFFFFFDE7), "🌫️"),
    InspectionRecord(6, "Jun 4, 2026",  "10:30 AM", "Healthy", "None",     "26.9°C", "69%", Color(0xFF1B5E20), Color(0xFFE8F5E9), "🌿"),
    InspectionRecord(7, "Jun 3, 2026",  "09:00 AM", "PLFD",    "Severe",   "31.2°C", "88%", Color(0xFFC62828), Color(0xFFFFEBEE), "🍂")
)

private val filters = listOf("All", "Healthy", "PLFD", "CLF", "Mildew")

// ── History Screen ─────────────────────────────────────────
@Composable
fun HistoryScreen(
    onBack: () -> Unit = {},
    onSelectRecord: () -> Unit = {}
) {
    var activeFilter by remember { mutableStateOf("All") }

    val filtered = if (activeFilter == "All") sampleRecords
    else sampleRecords.filter { it.result == activeFilter }

    val healthyCount  = sampleRecords.count { it.result == "Healthy" }
    val diseaseCount  = sampleRecords.count { it.result != "Healthy" }
    val totalCount    = sampleRecords.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistoryPageBg)
    ) {
        // ── Header ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(HistoryGreenDark)
                .padding(start = 20.dp, end = 20.dp, top = 52.dp, bottom = 16.dp)
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.15f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text("Inspection History",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.FilterList,
                        contentDescription = "Filter",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(14.dp))

            // Filter pills
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    val isActive = activeFilter == filter
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isActive) Color.White
                        else Color.White.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { activeFilter = filter }
                    ) {
                        Text(
                            text = filter,
                            color = if (isActive) HistoryGreenDark
                            else Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // ── Stats Row ───────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatCard("$healthyCount", "Healthy",
                Color(0xFF2E7D32), Modifier.weight(1f))
            StatCard("$diseaseCount", "Disease",
                Color(0xFFE65100), Modifier.weight(1f))
            StatCard("$totalCount",  "Total",
                Color(0xFF37474F), Modifier.weight(1f))
        }

        // ── Records List ────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filtered.forEach { record ->
                InspectionRecordRow(record = record, onClick = onSelectRecord)
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Stat Card ───────────────────────────────────────────────
@Composable
fun StatCard(value: String, label: String, valueColor: Color, modifier: Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HistoryCardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.Bold,
                fontSize = 20.sp, color = valueColor)
            Text(label, color = HistoryTextMuted, fontSize = 11.sp)
        }
    }
}

// ── Inspection Record Row ───────────────────────────────────
@Composable
fun InspectionRecordRow(record: InspectionRecord, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HistoryCardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji thumbnail
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(record.bg),
                contentAlignment = Alignment.Center
            ) {
                Text(record.emoji, fontSize = 24.sp)
            }

            Spacer(Modifier.width(12.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(record.result,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF1C1C1C))
                    Text(record.time,
                        color = HistoryTextMuted,
                        fontSize = 11.sp)
                }
                Text(record.date,
                    color = HistoryTextMuted,
                    fontSize = 11.sp)
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Temperature
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.Thermostat,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(13.dp))
                        Text(record.temp,
                            fontSize = 11.sp,
                            color = Color(0xFF666666))
                    }
                    // Humidity
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        Icon(Icons.Default.WaterDrop,
                            contentDescription = null,
                            tint = Color(0xFF0D47A1),
                            modifier = Modifier.size(13.dp))
                        Text(record.humidity,
                            fontSize = 11.sp,
                            color = Color(0xFF666666))
                    }
                    // Severity badge
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = record.bg
                    ) {
                        Text(record.severity,
                            color = record.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(
                                horizontal = 8.dp, vertical = 3.dp))
                    }
                }
            }

            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(18.dp))
        }
    }
}