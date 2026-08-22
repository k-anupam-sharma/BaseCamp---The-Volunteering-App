package com.example.basecamp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.basecamp.presentation.auth.LoginScreen
import com.example.basecamp.presentation.auth.SignupScreen
import com.example.basecamp.presentation.screens.organization.OrgDashboardScreen
import com.example.basecamp.presentation.screens.volunteer.VolunteerDashboardScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object VolunteerDashboard : Screen("volunteer_dashboard")
    object OrgDashboard : Screen("org_dashboard")
    object ScanTicket : Screen("scan_ticket")
}

@Composable
fun BaseCampNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onNavigateToSignup = {
                    navController.navigate(Screen.Signup.route)
                },
                onLoginSuccess = { role ->
                    // Route based on role
                    if (role == "Volunteer") {
                        navController.navigate(Screen.VolunteerDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.OrgDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(route = Screen.Signup.route) {
            SignupScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignupSuccess = { role ->
                    if (role == "Volunteer") {
                        navController.navigate(Screen.VolunteerDashboard.route) {
                            popUpTo(Screen.Signup.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.OrgDashboard.route) {
                            popUpTo(Screen.Signup.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(route = Screen.VolunteerDashboard.route) {
            VolunteerDashboardScreen()
        }

        composable(route = Screen.OrgDashboard.route) {
            OrgDashboardScreen(
                onNavigateToScan = { navController.navigate(Screen.ScanTicket.route) }
            )
        }
        
        composable(route = Screen.ScanTicket.route) {
            com.example.basecamp.presentation.screens.organization.ScanTicketScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}


