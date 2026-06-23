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
import com.example.rubberscan.navigation.MainScreen

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
                                navController.navigate("onboarding") {
                                    popUpTo("splash") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable("onboarding") {
                        OnboardingScreen(
                            onComplete = {
                                navController.navigate("welcome") {
                                    popUpTo("onboarding") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable("welcome") {
                        WelcomeScreen(
                            onGetStarted = {
                                navController.navigate("main")
                            },

                            onLogin = {
                                navController.navigate("login")
                            },

                            onRegister = {
                                navController.navigate("register")
                            },

                            onGuest = {
                                navController.navigate("main")
                            }
                        )
                    }

                    composable("main") {
                        MainScreen()
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

                    composable("history") {
                        HistoryScreen(
                            onBack = { navController.popBackStack() },
                            onHistoryDetail = {
                                navController.navigate("history-detail")
                            }
                        )
                    }

                    composable("history-detail") {
                        HistoryDetailScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("disease") {
                        DiseaseGuideScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}