package com.example.rubberscan

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rubberscan.ui.theme.*

// ── Guest Home Screen ────────────────────────────────────────
// Simplified home for guests: no sensor status, no history, no
// notifications — just the two features guests are allowed to use.
@Composable
fun GuestHomeScreen(
    onNavigate: (String) -> Unit = {},
    onSignUp: () -> Unit = {}
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GreenDark)
    ) {
        Column( modifier = Modifier.fillMaxSize()) {
            // ── Header ───────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1B5E20), GreenDark)
                        )
                    )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 26.dp)
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painterResource(R.drawable.app_logo_1),
                                contentDescription = null,
                                modifier = Modifier.size(45.dp)
                            )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text("Welcome to",
                                color = Color(0xFFA5D6A7), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Text("RubberScan", color = Color.White,
                                fontSize = 22.sp, fontWeight = FontWeight.Bold)

                        }
                        Spacer(Modifier.width(85.dp))
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White.copy(alpha = 0.15f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null,
                                    tint = Color.White, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Guest Mode", color = Color.White,
                                    fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = PageBg),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "What would you like to do?", fontWeight = FontWeight.Black,
                        fontSize = 16.sp, color = Color(0xFF4A4A4A)
                    )

                    GuestActionCard(
                        iconRes = R.drawable.scanner,
                        iconColor = GreenDark,
                        iconBg = GreenLight,
                        title = "Quick Scan",
                        subtitle = "Scan a rubber leaf to detect disease",
                        onClick = { onNavigate("scan") }
                    )

                    GuestActionCard(
                        iconRes = R.drawable.info,
                        iconColor = OrangeDark,
                        iconBg = OrangeLight,
                        title = "Disease Guide",
                        subtitle = "Learn about rubber leaf diseases",
                        onClick = { onNavigate("disease-guide") }
                    )

                    Spacer(Modifier.height(8.dp))

                    // ── Sign-up upsell ─────────────────────────
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                "Unlock more features", fontWeight = FontWeight.Bold,
                                fontSize = 15.sp, color = TextPrimary
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                "Sign up to save scan history, pair your sensor, and track your plantation.",
                                fontSize = 13.sp, color = TextMuted, lineHeight = 18.sp
                            )
                            Spacer(Modifier.height(14.dp))
                            Button(
                                onClick = onSignUp,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenDark)
                            ) {
                                Text("Sign Up")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GuestActionCard(
    iconRes: Int,
    iconColor: Color,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(28.dp),
                    colorFilter = ColorFilter.tint(iconColor)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(subtitle, fontSize = 12.sp, color = TextMuted, lineHeight = 16.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null,
                tint = Color(0xFF9CA3AF), modifier = Modifier.size(20.dp))
        }
    }
}
