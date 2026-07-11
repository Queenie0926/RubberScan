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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rubberscan.ui.theme.GreenDark
import com.example.rubberscan.ui.theme.RubberScanTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.LaunchedEffect
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts


private val bottomNavRoutes = setOf(
    "home", "scan", "history", "disease-guide", "profile", "ble-pairing"
)

// Routes that require a logged-in account
private val guestRestrictedRoutes = setOf("history", "profile")


class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()
    private val bleViewModel: BleViewModel by viewModels()

    private val settingsViewModel: SettingsViewModel by viewModels()
    private val notifViewModel: NotificationViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createChannels(this)

        val savedAutoReconnect = settingsViewModel.autoReconnect.value
        bleViewModel.autoReconnect = savedAutoReconnect

        val savedNotifications = settingsViewModel.notifications.value   // ← add
        bleViewModel.notificationsEnabled = savedNotifications

        setContent {
            RubberScanTheme {
                val nav = rememberNavController()
                val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route
                val currentUser by authViewModel.currentUser.collectAsState()
                val isGuest = currentUser == null

                    var showGuestDialog by remember { mutableStateOf(false) }
                    // ── Display name state ───────────────────────────────────
                    var displayName by remember { mutableStateOf("") }
                    LaunchedEffect(currentUser) {
                        displayName = currentUser?.name ?: ""
                    }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notifPermission = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { /* permission result, no action needed */ }

                    LaunchedEffect(Unit) {
                        notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

// ── Push sensor events to notification panel ─────────────
                val bleStateForNotif by bleViewModel.bleState.collectAsState()
                LaunchedEffect(bleStateForNotif) {
                    when (bleStateForNotif) {
                        BleState.DISCONNECTED -> notifViewModel.add(
                            AppNotification(
                                title   = "Sensor Disconnected",
                                message = "Your BLE sensor has disconnected from RubberScan.",
                                type    = NotifType.SENSOR
                            )
                        )
                        BleState.CONNECTED -> notifViewModel.add(
                            AppNotification(
                                title   = "Sensor Connected",
                                message = "Successfully connected to ${bleViewModel.connectedName.value.ifBlank { "your sensor" }}.",
                                type    = NotifType.SENSOR
                            )
                        )
                        else -> Unit
                    }
                }

                    // ── Guest gate dialog ────────────────────────
                    if (showGuestDialog) {
                        AlertDialog(
                            onDismissRequest = { showGuestDialog = false },
                            title = {
                                Text("Account Required", fontWeight = FontWeight.Bold)
                            },
                            text = {
                                Text("This feature is only available to logged-in users. Sign up or log in to access your history and profile.")
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showGuestDialog = false
                                        nav.navigate("login") {
                                            popUpTo("home") { saveState = true }
                                        }
                                    },
                                    colors = ButtonDefaults.textButtonColors(contentColor = GreenDark)
                                ) {
                                    Text("Log In / Sign Up", fontWeight = FontWeight.SemiBold)
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showGuestDialog = false },
                                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                                ) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                    Scaffold(
                        bottomBar = {
                            if (currentRoute in bottomNavRoutes) {
                                AppBottomNavBar(
                                    currentRoute = currentRoute ?: "home",
                                    onNavigate = { route ->
                                        if (isGuest && route in guestRestrictedRoutes) {
                                            showGuestDialog = true
                                        } else {
                                            nav.navigate(route) {
                                                popUpTo("home") { saveState = true }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        val startDestination =
                            if (FirebaseAuth.getInstance().currentUser != null) "home" else "welcome"
                        NavHost(
                            navController = nav,
                            startDestination = startDestination,
                            modifier = androidx.compose.ui.Modifier.padding(innerPadding),
                            enterTransition = { slideInHorizontally(tween(280)) { it } },
                            exitTransition = { slideOutHorizontally(tween(280)) { -it } },
                            popEnterTransition = { slideInHorizontally(tween(280)) { -it } },
                            popExitTransition = { slideOutHorizontally(tween(280)) { it } }
                        ) {
                            composable("welcome") {
                                WelcomeScreen(
                                    onGetStarted = { nav.navigate("onboarding") },
                                    onGuest = {
                                        nav.navigate("home") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("login") {
                                LoginScreen(
                                    viewModel = authViewModel,
                                    onLoginSuccess = {
                                        nav.navigate("home") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    },
                                    onSignUp = { nav.navigate("signup") }
                                )
                            }

                            composable("signup") {
                                SignUpScreen(
                                    viewModel = authViewModel,
                                    onSignUpSuccess = {
                                        nav.navigate("home") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    },
                                    onLogin = { nav.navigate("login") }
                                )
                            }

                            composable("onboarding") {
                                OnboardingScreen(
                                    onComplete = { nav.navigate("signup") }
                                )
                            }

                            composable("home") {
                                HomeScreen(
                                    onNavigate = { route ->
                                        if (isGuest && route in guestRestrictedRoutes) {
                                            showGuestDialog = true
                                        } else {
                                            nav.navigate(route)
                                        }
                                    },
                                    userName = displayName,
                                    bleViewModel = bleViewModel,
                                    isGuest = isGuest,
                                    notifViewModel   = notifViewModel
                                )
                            }

                            composable("scan") {
                                val temperature by bleViewModel.temperature.collectAsState()
                                val humidity by bleViewModel.humidity.collectAsState()
                                val bleState by bleViewModel.bleState.collectAsState()

                                ScanScreen(
                                    onBack = { nav.popBackStack() },
                                    onCapture = { nav.navigate("processing") },
                                    temperature = temperature,
                                    humidity = humidity,
                                    isSensorConnected = bleState == BleState.CONNECTED
                                )
                            }

                            composable("processing") {
                                val notifications by settingsViewModel.notifications.collectAsState()
                                ProcessingScreen(
                                    onComplete = {
                                        // Push to in-app notification panel
                                        notifViewModel.add(
                                            AppNotification(
                                                title   = "Scan Complete",
                                                message = "PLFD detected — Severity: Mild. Tap to view details.",
                                                type    = NotifType.SCAN
                                            )
                                        )
                                        if (notifications) {
                                            NotificationHelper.notifyScanComplete(
                                                context  = this@MainActivity,
                                                result   = "PLFD",
                                                severity = "Mild"
                                            )
                                        }
                                        nav.navigate("result") {
                                            popUpTo("processing") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            composable("result") {
                                ResultScreen(
                                    onBack = { nav.popBackStack() },
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
                                val notifications by settingsViewModel.notifications.collectAsState()
                                val diseaseAlerts by settingsViewModel.diseaseAlerts.collectAsState()
                                val autoReconnect by settingsViewModel.autoReconnect.collectAsState()

                                SettingsScreen(
                                    onBack          = { nav.popBackStack() },
                                    onNavigate      = { route -> nav.navigate(route) },
                                    notifications   = notifications,
                                    onNotifications = { enabled ->
                                        settingsViewModel.setNotifications(enabled)
                                        bleViewModel.notificationsEnabled = enabled
                                    },
                                    diseaseAlerts   = diseaseAlerts,
                                    onDiseaseAlerts = { settingsViewModel.setDiseaseAlerts(it) },
                                    autoReconnect   = autoReconnect,
                                    onAutoReconnect = { enabled ->
                                        settingsViewModel.setAutoReconnect(enabled)
                                        bleViewModel.autoReconnect = enabled
                                    },
                                    onClearRecords  = { onDone -> authViewModel.clearAllScanHistory(onDone) }  // ← add
                                )
                            }

                            composable("history") {
                                HistoryScreen(
                                    onBack = { nav.popBackStack() },
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
                                    onBack = { nav.popBackStack() }
                                )
                            }

                            composable("profile") {
                                ProfileScreen(
                                    onBack = { nav.popBackStack() },
                                    onNavigate = { route -> nav.navigate(route) },
                                    onSignOut = {
                                        authViewModel.signOut()
                                        nav.navigate("welcome") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    },
                                    userName = displayName,
                                    userEmail = currentUser?.email ?: "",
                                    onNameChange = { newName ->
                                        displayName =
                                            newName                          // update UI instantly
                                        authViewModel.updateDisplayName(newName)       // persist to Firebase + Room
                                    }
                                )
                            }
                            composable("privacy-policy") {
                                PrivacyPolicyScreen(onBack = { nav.popBackStack() })
                            }
                        }
                    }
                }
            }
        }
    }
