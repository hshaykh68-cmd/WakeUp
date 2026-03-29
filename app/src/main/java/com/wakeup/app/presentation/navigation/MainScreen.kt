package com.wakeup.app.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wakeup.app.presentation.home.HomeScreen
import com.wakeup.app.presentation.alarms.AlarmListScreen
import com.wakeup.app.presentation.stats.StatsScreen
import com.wakeup.app.presentation.settings.SettingsScreen

@Composable
fun MainScreen(
    navController: NavHostController = rememberNavController()
) {
    val bottomNavItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Alarms,
        BottomNavItem.Stats,
        BottomNavItem.Settings
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = { Text(item.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onCreateAlarm = {
                        // Navigate to create alarm from home
                    },
                    onNavigateToAlarms = {
                        navController.navigate(Screen.Alarms.route)
                    }
                )
            }
            composable(Screen.Alarms.route) {
                AlarmListScreen(
                    onCreateAlarm = {
                        navController.navigate(Screen.CreateAlarm.route)
                    },
                    onEditAlarm = { alarmId ->
                        navController.navigate(Screen.EditAlarm.createRoute(alarmId))
                    }
                )
            }
            composable(Screen.Stats.route) {
                StatsScreen()
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onNavigateToPremium = {
                        navController.navigate(Screen.Premium.route)
                    }
                )
            }
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Home : BottomNavItem(
        Screen.Home.route,
        "Home",
        Icons.Filled.Home,
        Icons.Outlined.Home
    )
    data object Alarms : BottomNavItem(
        Screen.Alarms.route,
        "Alarms",
        Icons.Filled.Alarm,
        Icons.Outlined.Alarm
    )
    data object Stats : BottomNavItem(
        Screen.Stats.route,
        "Stats",
        Icons.Filled.BarChart,
        Icons.Outlined.BarChart
    )
    data object Settings : BottomNavItem(
        Screen.Settings.route,
        "Settings",
        Icons.Filled.Settings,
        Icons.Outlined.Settings
    )
}
