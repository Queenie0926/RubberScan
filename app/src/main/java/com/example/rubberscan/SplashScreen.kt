package com.example.rubberscan

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onComplete: () -> Unit
) {

    var progress by remember {
        mutableStateOf(0f)
    }

    LaunchedEffect(Unit) {

        while (progress < 1f) {
            delay(60)
            progress += 0.02f
        }

        delay(300)

        onComplete()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E20))
            .padding(
                horizontal = 32.dp,
                vertical = 64.dp
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(1.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "🌳",
                    fontSize = 100.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Luntian",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Para sa mga mag-uuma,\npara sa mabungahong ugma",
                color = Color(0xFFA5D6A7),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(
                        Color(0xFF4CAF50).copy(alpha = 0.5f)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                color = Color.White.copy(alpha = 0.1f),
                shape = RoundedCornerShape(50.dp)
            ) {

                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 10.dp
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    val infinite =
                        rememberInfiniteTransition()

                    val alpha by infinite.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec =
                            infiniteRepeatable(
                                animation = tween(
                                    1000
                                ),
                                repeatMode =
                                    RepeatMode.Reverse
                            )
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(alpha)
                            .background(
                                Color(0xFF66BB6A),
                                CircleShape
                            )
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "RubberGuard",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text =
                    "Protecting Rubber Plantations Through Smart Detection",
                color = Color(0xFFC8E6C9),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFF66BB6A),
                trackColor =
                    Color.White.copy(alpha = 0.2f)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Loading...",
                color = Color(0xFFA5D6A7),
                fontSize = 12.sp
            )
        }
    }
}