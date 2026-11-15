package com.jarvis.android.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jarvis.android.ui.screens.*

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Chat : Screen("chat")
    object Permissions : Screen("permissions")
    object Automations : Screen("automations")
    object Dashboard : Screen("dashboard")
    object Settings : Screen("settings")
    object VoiceSetup : Screen("voice_setup")
}

@Composable
fun JarvisNavHost() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Screen.Onboarding.route) {
        composable(Screen.Onboarding.route) { OnboardingScreen(nav) }
        composable(Screen.Chat.route) { ChatScreen(nav) }
        composable(Screen.Permissions.route) { PermissionsHubScreen(nav) }
        composable(Screen.Automations.route) { AutomationsScreen(nav) }
        composable(Screen.Dashboard.route) { DashboardScreen(nav) }
        composable(Screen.Settings.route) { SettingsScreen(nav) }
        composable(Screen.VoiceSetup.route) { VoiceSetupScreen(nav) }
    }
}
