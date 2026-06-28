package com.example.rubberscan.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.graphics.Color
import com.example.rubberscan.ui.theme.*

val bottomItems = listOf(
    BottomNavItem(
        route = "home",
        label = "Home",
        icon = Icons.Outlined.Home
    ),
    BottomNavItem(
        route = "scan",
        label = "Scan",
        icon = Icons.Outlined.CameraAlt
    ),
    BottomNavItem(
        route = "history",
        label = "History",
        icon = Icons.Outlined.History
    ),
    BottomNavItem(
        route = "guide",
        label = "Guide",
        icon = Icons.Outlined.MenuBook
    ),
    BottomNavItem(
        route = "profile",
        label = "Profile",
        icon = Icons.Outlined.Person
    )
)

@Composable
fun RubberScanBottomBar(
    navController: NavHostController
) {

    val currentRoute =
        navController.currentBackStackEntryAsState()
            .value?.destination?.route

    NavigationBar(
        containerColor = Color.White
    ) {

        bottomItems.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,

                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },

                label = {
                    Text(item.label)
                },

                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2E7D32),
                    selectedTextColor = Color(0xFF2E7D32),
                    indicatorColor = Color(0xFFE8F5E9),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray
                )
            )
        }
    }
}