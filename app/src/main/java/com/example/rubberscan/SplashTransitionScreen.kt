package com.example.rubberscan

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// ── Splash Transition Screen ─────────────────────────────────
// Reusable: shows a tailored message for a moment, then auto-
// advances. Used after RubberOwnershipScreen to bridge into the
// account flow with copy that matches the user's Yes/No answer.
@Composable
fun SplashTransitionScreen(
    title: String,
    subtitle: String,
    onFinished: () -> Unit,
    delayMillis: Long = 2000L
) {
    LaunchedEffect(Unit) {
        delay(delayMillis)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFe8f5e9), Color(0xFFB2F2C2)))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painterResource(R.drawable.app_logo),
                contentDescription = null,
                modifier = Modifier.size(100.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1B5E20),
                textAlign = TextAlign.Center,
                lineHeight = 28.sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                subtitle,
                fontSize = 14.sp,
                color = Color(0xFF424242),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            Spacer(Modifier.height(28.dp))
            CircularProgressIndicator(
                color = Color(0xFF1B5E20),
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp
            )
        }
    }
}
