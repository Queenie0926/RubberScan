package com.example.rubberscan.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*

import com.example.rubberscan.HomeScreen
import com.example.rubberscan.ScanScreen
import com.example.rubberscan.HistoryScreen
import com.example.rubberscan.ProfileScreen
import com.example.rubberscan.DiseaseGuideScreen

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            RubberScanBottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {

            composable("home") {
                HomeScreen(
                    onNavigate = {}
                )
            }

            composable("scan") {
                ScanScreen()
            }

            composable("history") {
                HistoryScreen(
                    onBack = {},
                    onHistoryDetail = {}
                )
            }

            composable("guide") {
                DiseaseGuideScreen(
                    onBack = {}
                )
            }

            composable("profile") {
                ProfileScreen()
            }
        }
    }
}