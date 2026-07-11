package com.example.rubberscan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rubberscan.ui.theme.*

// ── Privacy Policy Screen ───────────────────────────────────
@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // ── Header ──────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 20.dp),
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
            Spacer(Modifier.width(12.dp))
            Text("Privacy Policy", color = Color.White,
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        // ── Content ─────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Intro
            Text(
                "This Privacy Policy explains how RubberScan collects, uses, and protects your information. By using this application, you agree to the practices described below.",
                fontSize = 13.sp,
                color = TextMuted,
                lineHeight = 20.sp
            )

            HorizontalDivider(color = BorderGray)

            // Section 1
            PolicySection(
                number = "1",
                title  = "Data We Collect",
                body   = buildString {
                    appendLine("RubberScan collects the following types of data to provide its core functionality:")
                    appendLine()
                    appendLine("• Camera — Used exclusively to capture rubber leaf images for disease detection. Images are processed locally on your device and are not uploaded to any external server.")
                    appendLine()
                    appendLine("• BLE Sensor Data — Temperature and humidity readings are received from your paired ESP32 sensor via Bluetooth Low Energy. This data is displayed in real time and stored alongside scan results.")
                    appendLine()
                    append("• Scan History — Each inspection result, including the detected disease, severity grade, confidence score, sensor readings, date, and location label, is stored locally on your device.")
                }
            )

            HorizontalDivider(color = BorderGray)

            // Section 2
            PolicySection(
                number = "2",
                title  = "How Data Is Stored",
                body   = buildString {
                    appendLine("Your data is stored in two places:")
                    appendLine()
                    appendLine("• Local Database (Room/SQLite) — Scan history and user profile information are stored directly on your device using Android's Room database. This data remains on your device unless you choose to delete it.")
                    appendLine()
                    append("• Firebase (Cloud) — Your account information (name and email) is stored securely in Firebase Authentication and is used solely for login and profile management. We do not store scan images or sensor data in the cloud.")
                }
            )

            HorizontalDivider(color = BorderGray)

            // Section 3
            PolicySection(
                number = "3",
                title  = "Data Sharing Policy",
                body   = buildString {
                    appendLine("RubberScan does not sell, trade, or share your personal data with third parties.")
                    appendLine()
                    appendLine("• Your scan history and sensor data are never transmitted outside your device.")
                    appendLine()
                    appendLine("• Your account credentials are managed by Firebase Authentication, which follows Google's privacy and security standards.")
                    appendLine()
                    append("• This application was developed for academic research purposes as a capstone project at the University of Mindanao. Data collected is not used for commercial purposes.")
                }
            )

            HorizontalDivider(color = BorderGray)

            // Section 4
            PolicySection(
                number = "4",
                title  = "Your Rights",
                body   = buildString {
                    appendLine("As a RubberScan user, you have the following rights over your data:")
                    appendLine()
                    appendLine("• Delete Scan History — You may clear all inspection records at any time through Settings → Data Storage → Clear Old Records.")
                    appendLine()
                    append("• Delete Account — Signing out of the app removes your active session. To permanently delete your account and associated data, contact the development team.")
                }
            )

            HorizontalDivider(color = BorderGray)

            // Footer
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Effective Date: June 2026",
                    fontSize = 12.sp, color = TextMuted)
                Text("Developed by: Ellaine C. Musni, Charish D. Dacillo, Queenie A. Doringo",
                    fontSize = 12.sp, color = TextMuted, lineHeight = 18.sp)
                Text("BS Computer Engineering · University of Mindanao",
                    fontSize = 12.sp, color = TextMuted)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ── Policy Section ──────────────────────────────────────────
@Composable
private fun PolicySection(number: String, title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(GreenLight),
                contentAlignment = Alignment.Center
            ) {
                Text(number, fontSize = 13.sp,
                    fontWeight = FontWeight.Bold, color = GreenDark)
            }
            Spacer(Modifier.width(10.dp))
            Text(title, fontSize = 15.sp,
                fontWeight = FontWeight.Bold, color = Color(0xFF1C1C1C))
        }
        Text(body, fontSize = 13.sp,
            color = Color(0xFF424242), lineHeight = 20.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun PrivacyPolicyScreenPreview() {
    PrivacyPolicyScreen()
}