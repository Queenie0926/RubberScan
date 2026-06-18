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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rubberscan.SplashScreen
import com.example.rubberscan.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            RubberScanTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(
                            onComplete = {
                                navController.navigate("welcome")
                            }
                        )
                    }

                    composable("welcome") {
                        WelcomeScreen(
                            onGetStarted = {},
                            onLogin = {},
                            onRegister = {},
                            onGuest = {
                                navController.navigate("home")
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            onNavigate = { route ->
                                navController.navigate(route)
                            }
                        )
                    }

                    composable("ble-pairing") {
                        BLEPairingScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}