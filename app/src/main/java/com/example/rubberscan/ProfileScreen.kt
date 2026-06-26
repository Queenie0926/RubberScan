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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ── Colour tokens ──────────────────────────────────────────
private val ProfileGreenDark = Color(0xFF1B5E20)
private val ProfilePageBg    = Color(0xFFF1F8F1)
private val ProfileCardBg    = Color(0xFFFFFFFF)
private val ProfileTextMuted = Color(0xFF9CA3AF)
private val OrangeDark  = Color(0xFFE65100)
private val OrangeLight = Color(0xFFFFF3E0)

// ── Data models ────────────────────────────────────────────
data class ProfileMenuItem(
    val icon: ImageVector,
    val iconTint: Color,
    val label: String,
    val bg: Color,
    val count: String?,
    val route: String
)

data class ProfileStat(
    val label: String,
    val value: String,
    val color: Color
)

// ── Sample data ────────────────────────────────────────────
private val menuSection1 = listOf(
    ProfileMenuItem(Icons.Default.History, Color(0xFF0D47A1), "Saved Inspections", Color(0xFFE3F2FD), "24", "history"),
    ProfileMenuItem(Icons.Default.Bluetooth, Color(0xFF00695C), "Sensor Settings", Color(0xFFE0F2F1), null, "ble-pairing")
)

private val menuSection2 = listOf(
    ProfileMenuItem(Icons.Default.Settings, Color(0xFF546E7A), "Account Settings", Color(0xFFECEFF1), null, "settings"),
    ProfileMenuItem(Icons.Default.MenuBook, OrangeDark, "Disease Guide", OrangeLight, null, "disease-guide")
)

private val profileStats = listOf(
    ProfileStat("Inspections", "24", Color(0xFF1B5E20)),
    ProfileStat("Disease Found", "7", Color(0xFFE65100)),
    ProfileStat("Healthy", "17", Color(0xFF4CAF50))
)

// ── Profile Screen ─────────────────────────────────────────
@Composable
fun ProfileScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfilePageBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ──────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ProfileGreenDark)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 80.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
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
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Back",
                        tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Text("Profile", color = Color.White,
                    fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit",
                        tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        // ── Avatar Card (overlaps header) ────────────────────
        Box(modifier = Modifier
            .padding(horizontal = 16.dp)
            .offset(y = (-64).dp)
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = ProfileCardBg),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF66BB6A), Color(0xFF1B5E20))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("JD", color = Color.White,
                            fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Juan dela Cruz", fontWeight = FontWeight.Bold,
                            fontSize = 16.sp, color = Color(0xFF1C1C1C))
                        Spacer(Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null,
                                tint = ProfileTextMuted, modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(3.dp))
                            Text("Marilog District, Davao City",
                                color = ProfileTextMuted, fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFFE8F5E9)) {
                            Text("Rubber Farmer", color = Color(0xFF1B5E20),
                                fontSize = 11.sp, fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                    }
                }
            }
        }

        // ── Stats ───────────────────────────────────────────
        Box(modifier = Modifier
            .padding(horizontal = 16.dp)
            .offset(y = (-52).dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ProfileCardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(vertical = 12.dp)) {
                    profileStats.forEachIndexed { index, stat ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(
                                    if (index > 0) Modifier.drawWithBorderStart() else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stat.value, fontWeight = FontWeight.Black,
                                    fontSize = 22.sp, color = stat.color)
                                Text(stat.label, color = ProfileTextMuted, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // ── Menu Sections ─────────────────────────────────────
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-40).dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuSectionCard(items = menuSection1, onNavigate = onNavigate)
            MenuSectionCard(items = menuSection2, onNavigate = onNavigate)

            // About card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ProfileCardBg),
                elevation = CardDefaults.cardElevation(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("About RubberScan", fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp, color = Color(0xFF424242))
                        Text("Version 1.0.0 · Capstone Project",
                            color = ProfileTextMuted, fontSize = 11.sp)
                    }
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFE8F5E9)) {
                        Text("v1.0.0", color = Color(0xFF1B5E20),
                            fontSize = 11.sp, fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }

            // Sign Out button
            OutlinedButton(
                onClick = { onNavigate("login") },
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFFCDD2)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null,
                    modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Sign Out", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Menu Section Card ───────────────────────────────────────
@Composable
fun MenuSectionCard(items: List<ProfileMenuItem>, onNavigate: (String) -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ProfileCardBg),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            items.forEachIndexed { index, item ->
                if (index > 0) {
                    HorizontalDivider(color = Color(0xFFF0F0F0))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(item.route) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(item.bg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(item.icon, contentDescription = item.label,
                            tint = item.iconTint, modifier = Modifier.size(18.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(item.label, fontWeight = FontWeight.Medium,
                        fontSize = 14.sp, color = Color(0xFF1C1C1C),
                        modifier = Modifier.weight(1f))
                    item.count?.let { count ->
                        Surface(shape = RoundedCornerShape(50), color = Color(0xFFE8F5E9)) {
                            Text(count, color = Color(0xFF1B5E20),
                                fontSize = 11.sp, fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                        Spacer(Modifier.width(6.dp))
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null,
                        tint = Color(0xFFD1D5DB), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

// ── Helper: thin left border for stat dividers ───────────────
fun Modifier.drawWithBorderStart(): Modifier = this.then(
    Modifier.padding(start = 1.dp)
)