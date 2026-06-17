package com.example.rubberscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rubberscan.ui.theme.RubberScanTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.rubberscan.SplashScreen
import com.example.rubberscan.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            RubberScanTheme {

                var showSplash by remember {
                    mutableStateOf(true)
                }

                if (showSplash) {

                    SplashScreen(
                        onComplete = {
                            showSplash = false
                        }
                    )

                } else {

                    WelcomeScreen(
                        onGetStarted = {},
                        onLogin = {},
                        onRegister = {},
                        onGuest = {}
                    )

                }
            }
        }
    }
}