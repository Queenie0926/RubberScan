package com.example.rubberscan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
@Composable
fun WelcomeScreen(
    onGetStarted: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onGuest: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B5E20))
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {

                    Box(
                        modifier = Modifier
                            .size(96.dp),
                        contentAlignment = Alignment.Center
                    ) {

                        Text(
                            text = "🌳",
                            fontSize = 42.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "BantayGoma",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Para sa mga mag-uuma,\nPara sa ugma nga mabungahon",
                    color = Color(0xFFA5D6A7),
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp),
                    textAlign = TextAlign.Center
                )


            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(
                    topStart = 32.dp,
                    topEnd = 32.dp
                )
            ) {

                Column(
                    modifier = Modifier.padding(24.dp)
                ) {

                    Button(
                        onClick = onGetStarted,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1B5E20)
                        )
                    ) {

                        Text("Get Started")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        OutlinedButton(
                            onClick = onLogin,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF2E7D32)
                            ),
                            border = BorderStroke(
                                1.dp,
                                Color(0xFF2E7D32)
                            )
                        ) {

                            Icon(
                                Icons.Outlined.Login,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("Login")
                        }

                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )

                        OutlinedButton(
                            onClick = onRegister,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFF1565C0)
                            ),
                            border = BorderStroke(
                                1.dp,
                                Color(0xFF1565C0)
                            )
                        ) {

                            Icon(
                                Icons.Outlined.PersonAdd,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(6.dp)
                            )

                            Text("Register")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = onGuest,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Black
                        )
                    ) {

                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = null
                        )

                        Spacer(
                            modifier = Modifier.width(6.dp)
                        )

                        Text("Continue as Guest")
                    }
                }
            }
        }
    }
}