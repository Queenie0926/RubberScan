package com.example.rubberscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rubberscan.ui.theme.RubberScanTheme

private val bottomNavRoutes = setOf(
    "home", "scan", "history", "disease-guide", "profile", "ble-pairing"
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RubberScanTheme {
                val nav          = rememberNavController()
                val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route

                Scaffold(
                    bottomBar = {
                        if (currentRoute in bottomNavRoutes) {
                            AppBottomNavBar(
                                currentRoute = currentRoute ?: "home",
                                onNavigate   = { route ->
                                    nav.navigate(route) {
                                        popUpTo("home") { saveState = true }
                                        launchSingleTop = true
                                        restoreState    = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController      = nav,
                        startDestination   = "splash",
                        modifier           = androidx.compose.ui.Modifier.padding(innerPadding),
                        enterTransition    = { slideInHorizontally(tween(280)) { it } },
                        exitTransition     = { slideOutHorizontally(tween(280)) { -it } },
                        popEnterTransition = { slideInHorizontally(tween(280)) { -it } },
                        popExitTransition  = { slideOutHorizontally(tween(280)) { it } }
                    ) {

                        composable("splash") {
                            SplashScreen(
                                onComplete = { nav.navigate("welcome") {
                                    popUpTo("splash") { inclusive = true }
                                }}
                            )
                        }

                        composable("welcome") {
                            WelcomeScreen(
                                onGetStarted = { nav.navigate("onboarding") },
                                onLogin      = { nav.navigate("home") },
                                onRegister   = { nav.navigate("home") },
                                onGuest      = { nav.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }}
                            )
                        }

                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = { nav.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }}
                            )
                        }

                        composable("home") {
                            HomeScreen(onNavigate = { route -> nav.navigate(route) })
                        }

                        composable("scan") {
                            ScanScreen(
                                onBack    = { nav.popBackStack() },
                                onCapture = { nav.navigate("processing") }
                            )
                        }

                        composable("processing") {
                            ProcessingScreen(
                                onComplete = { nav.navigate("result") {
                                    popUpTo("processing") { inclusive = true }
                                }}
                            )
                        }

                        composable("result") {
                            ResultScreen(
                                onBack     = { nav.popBackStack() },
                                onNavigate = { route -> nav.navigate(route) }
                            )
                        }

                        composable("severity") {
                            SeverityScreen(onBack = { nav.popBackStack() })
                        }

                        composable("environmental-risk") {
                            EnvironmentalRiskScreen(onBack = { nav.popBackStack() })
                        }

                        composable("recommendation") {
                            RecommendationScreen(onBack = { nav.popBackStack() })
                        }

                        composable("early-warning") {
                            EarlyWarningScreen(onBack = { nav.popBackStack() })
                        }

                        composable("settings") {
                            SettingsScreen(
                                onBack     = { nav.popBackStack() },
                                onNavigate = { route -> nav.navigate(route) }
                            )
                        }

                        composable("history") {
                            HistoryScreen(
                                onBack          = { nav.popBackStack() },
                                onHistoryDetail = { nav.navigate("history-detail") }
                            )
                        }

                        composable("history-detail") {
                            HistoryDetailScreen(onBack = { nav.popBackStack() })
                        }

                        composable("disease-guide") {
                            DiseaseGuideScreen(onBack = { nav.popBackStack() })
                        }

                        composable("ble-pairing") {
                            BLEPairingScreen(onBack = { nav.popBackStack() })
                        }

                        composable("profile") {
                            ProfileScreen(
                                onBack     = { nav.popBackStack() },
                                onNavigate = { route -> nav.navigate(route) }
                            )
                        }
                    }
                }
            }
        }
    }
}
