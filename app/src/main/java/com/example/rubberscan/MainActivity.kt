package com.example.rubberscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut


private val bottomNavRoutes = setOf(
    "home", "history", "disease-guide", "profile", "ble-pairing"
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

        // These stay here — outside setContent is fine for non-compose code
        val savedAutoReconnect = settingsViewModel.autoReconnect.value
        bleViewModel.autoReconnect = savedAutoReconnect
        val savedNotifications = settingsViewModel.notifications.value
        bleViewModel.notificationsEnabled = savedNotifications

        setContent {
            RubberScanTheme {
                val nav = rememberNavController()
                val currentRoute = nav.currentBackStackEntryAsState().value?.destination?.route
                val currentUser by authViewModel.currentUser.collectAsState()
                val isGuest = currentUser == null

                var showGuestDialog by remember { mutableStateOf(false) }
                var displayName by remember { mutableStateOf("") }
                LaunchedEffect(currentUser) {
                    displayName = currentUser?.name ?: ""
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val notifPermission = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { }
                    LaunchedEffect(Unit) {
                        notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

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

                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = currentRoute != null && currentRoute in bottomNavRoutes && !isGuest,
                            enter   = fadeIn(),
                            exit    = fadeOut()
                        ) {
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
                    // ← startDestination stays here, inside setContent
                    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "welcome"

                    NavHost(
                        navController      = nav,
                        startDestination   = startDestination,
                        modifier           = androidx.compose.ui.Modifier.padding(innerPadding),
                        enterTransition    = { slideInHorizontally(tween(220)) { it } },
                        exitTransition     = { slideOutHorizontally(tween(220)) { -it } },
                        popEnterTransition = { slideInHorizontally(tween(220)) { -it } },
                        popExitTransition  = { slideOutHorizontally(tween(220)) { it } }
                    ) {
                        composable("welcome") {
                            WelcomeScreen(
                                onGetStarted = { nav.navigate("onboarding") }
                            )
                        }
                        composable("login") {
                            BackHandler(enabled = true) { this@MainActivity.moveTaskToBack(true) }
                            LoginScreen(
                                viewModel      = authViewModel,
                                onLoginSuccess = { nav.navigate("home") { popUpTo("welcome") { inclusive = true } } },
                                onSignUp       = { nav.navigate("signup") },
                                onGuest      = { nav.navigate("home") { popUpTo("welcome") { inclusive = true } } }
                            )
                        }
                        composable("signup") {
                            BackHandler(enabled = true) { this@MainActivity.moveTaskToBack(true) }
                            SignUpScreen(
                                viewModel       = authViewModel,
                                onSignUpSuccess = { nav.navigate("home") { popUpTo("welcome") { inclusive = true } } },
                                onLogin         = { nav.navigate("login") },
                                onGuest      = { nav.navigate("home") { popUpTo("welcome") { inclusive = true } } }
                            )
                        }
                        composable("onboarding") {
                            OnboardingScreen(
                                onComplete = {
                                    nav.navigate("signup") { popUpTo("welcome") { inclusive = true } }
                                }
                            )
                        }
                        composable("home") {
                            BackHandler(enabled = true) { this@MainActivity.moveTaskToBack(true) }
                            if (isGuest) {
                                GuestHomeScreen(
                                    onNavigate = { route -> nav.navigate(route) },
                                    onSignUp   = {
                                        nav.navigate("signup") {
                                            popUpTo("home") { saveState = true }
                                        }
                                    }
                                )
                            } else {
                                HomeScreen(
                                    onNavigate = { route ->
                                        nav.navigate(route) {
                                            popUpTo("home") { saveState = true }
                                            launchSingleTop = true
                                            restoreState    = true
                                        }
                                    },
                                    userName       = displayName,
                                    bleViewModel   = bleViewModel,
                                    isGuest        = false,
                                    notifViewModel = notifViewModel
                                )
                            }
                        }
                        composable("scan") {
                            val temperature by bleViewModel.temperature.collectAsState()
                            val humidity    by bleViewModel.humidity.collectAsState()
                            val bleState    by bleViewModel.bleState.collectAsState()
                            ScanScreen(
                                onBack            = { nav.popBackStack() },
                                onCapture         = { nav.navigate("processing") },
                                temperature       = temperature,
                                humidity          = humidity,
                                isSensorConnected = bleState == BleState.CONNECTED,
                                isGuest           = isGuest
                            )
                        }
                        composable("processing") {
                            val notifications by settingsViewModel.notifications.collectAsState()
                            ProcessingScreen(
                                onComplete = {
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
                                onBack     = { nav.popBackStack() },
                                onNavigate = { route -> nav.navigate(route) }
                            )
                        }
                        composable("severity") { SeverityScreen(onBack = { nav.popBackStack() }) }
                        composable("environmental-risk") { EnvironmentalRiskScreen(onBack = { nav.popBackStack() }) }
                        composable("recommendation") { RecommendationScreen(onBack = { nav.popBackStack() }) }
                        composable("early-warning") { EarlyWarningScreen(onBack = { nav.popBackStack() }) }
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
                                onClearRecords  = { onDone -> authViewModel.clearAllScanHistory(onDone) }
                            )
                        }
                        composable("history") {
                            HistoryScreen(
                                onBack          = { nav.popBackStack() },
                                onHistoryDetail = { nav.navigate("history-detail") }
                            )
                        }
                        composable("history-detail") { HistoryDetailScreen(onBack = { nav.popBackStack() }) }
                        composable("disease-guide") { DiseaseGuideScreen(onBack = { nav.popBackStack() }) }
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
                                onLogout  = {
                                    authViewModel.signOut()
                                    nav.navigate("login") { popUpTo(0) { inclusive = true } }
                                },
                                userName     = displayName,
                                userEmail    = currentUser?.email ?: "",
                                onNameChange = { newName ->
                                    displayName = newName
                                    authViewModel.updateDisplayName(newName)
                                }
                            )
                        }
                        composable("privacy-policy") { PrivacyPolicyScreen(onBack = { nav.popBackStack() }) }
                    }
                }
            }
        }
    }}