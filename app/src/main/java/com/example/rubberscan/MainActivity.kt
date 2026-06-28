package com.example.rubberscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.rubberscan.ui.theme.RubberScanTheme
import com.google.firebase.auth.FirebaseAuth

private val bottomNavRoutes = setOf(
    "home", "scan", "history", "disease-guide", "profile", "ble-pairing"
)

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val bleViewModel: BleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            RubberScanTheme {
                val nav          = rememberNavController()
                val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route
                val currentUser  by authViewModel.currentUser.collectAsState()

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
                    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "welcome"
                    NavHost(
                        navController      = nav,
                        startDestination   = startDestination,
                        modifier           = androidx.compose.ui.Modifier.padding(innerPadding),
                        enterTransition    = { slideInHorizontally(tween(280)) { it } },
                        exitTransition     = { slideOutHorizontally(tween(280)) { -it } },
                        popEnterTransition = { slideInHorizontally(tween(280)) { -it } },
                        popExitTransition  = { slideOutHorizontally(tween(280)) { it } }
                    ) {
                        composable("welcome") {
                            WelcomeScreen(
                                onGetStarted = { nav.navigate("onboarding") },
                                onGuest      = { nav.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }}
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                viewModel      = authViewModel,
                                onLoginSuccess = { nav.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }},
                                onSignUp       = { nav.navigate("signup") }
                            )
                        }

                        composable("signup") {
                            SignUpScreen(
                                viewModel       = authViewModel,
                                onSignUpSuccess = { nav.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }},
                                onLogin         = { nav.navigate("login") }
                            )
                        }

                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = { nav.navigate("signup") }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigate   = { route -> nav.navigate(route) },
                                userName     = currentUser?.name ?: "",
                                bleViewModel = bleViewModel
                            )
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
                            BLEPairingScreen(
                                viewModel = bleViewModel,
                                onBack    = { nav.popBackStack() }
                            )
                        }

                        composable("profile") {
                            ProfileScreen(
                                onBack     = { nav.popBackStack() },
                                onNavigate = { route -> nav.navigate(route) },
                                onSignOut  = {
                                    authViewModel.signOut()
                                    nav.navigate("welcome") {
                                        popUpTo(0) { inclusive = true }
                                    }
                                },
                                userName   = currentUser?.name ?: "",
                                userEmail  = currentUser?.email ?: ""
                            )
                        }
                    }
                }
            }
        }
    }
}
