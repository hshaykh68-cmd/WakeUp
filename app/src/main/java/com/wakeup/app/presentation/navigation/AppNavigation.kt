package com.wakeup.app.presentation.navigation

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.wakeup.app.presentation.onboarding.OnboardingScreen
import com.wakeup.app.presentation.onboarding.PermissionSetupScreen
import com.wakeup.app.presentation.onboarding.SplashScreen
import com.wakeup.app.presentation.alarm.CreateAlarmScreen
import com.wakeup.app.presentation.alarm.EditAlarmScreen
import com.wakeup.app.presentation.alarm.AlarmRingingActivity
import com.wakeup.app.presentation.alarm.MissionScreen
import com.wakeup.app.presentation.alarm.WakeSuccessScreen
import com.wakeup.app.presentation.premium.PremiumScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToPermissionSetup = {
                    navController.navigate(Screen.PermissionSetup.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onFinish = {
                    navController.navigate(Screen.PermissionSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PermissionSetup.route) {
            PermissionSetupScreen(
                onPermissionsGranted = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.PermissionSetup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen()
        }

        composable(Screen.CreateAlarm.route) {
            CreateAlarmScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.EditAlarm.route) { backStackEntry ->
            val alarmId = backStackEntry.arguments?.getString("alarmId")
            alarmId?.let {
                EditAlarmScreen(
                    alarmId = it,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(Screen.Premium.route) {
            PremiumScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
