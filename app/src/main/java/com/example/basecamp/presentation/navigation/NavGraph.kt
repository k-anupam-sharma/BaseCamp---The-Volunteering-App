package com.example.basecamp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.basecamp.presentation.auth.LoginScreen
import com.example.basecamp.presentation.auth.SignupScreen
import com.example.basecamp.presentation.auth.CompleteProfileScreen
import com.example.basecamp.presentation.screens.profile.ProfileScreen
import com.example.basecamp.presentation.screens.organization.OrgDashboardScreen
import com.example.basecamp.presentation.screens.volunteer.VolunteerDashboardScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object VolunteerDashboard : Screen("volunteer_dashboard")
    object OrgDashboard : Screen("org_dashboard")
    object ScanTicket : Screen("scan_ticket")
    object CompleteProfile : Screen("complete_profile/{userId}/{email}/{name}") {
        fun createRoute(userId: String, email: String, name: String) = "complete_profile/$userId/$email/$name"
    }
    object Profile : Screen("profile")
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
                onNeedsProfileSetup = { userId, email, name ->
                    val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                    navController.navigate(Screen.CompleteProfile.createRoute(userId, encodedEmail, encodedName))
                },
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
                onNeedsProfileSetup = { userId, email, name ->
                    val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                    navController.navigate(Screen.CompleteProfile.createRoute(userId, encodedEmail, encodedName))
                },
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

        composable(
            route = Screen.CompleteProfile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val email = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", "UTF-8")
            val name = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("name") ?: "", "UTF-8")
            
            CompleteProfileScreen(
                userId = userId,
                initialEmail = email,
                initialName = name,
                onSetupSuccess = { role ->
                    if (role == "Volunteer") {
                        navController.navigate(Screen.VolunteerDashboard.route) {
                            popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.OrgDashboard.route) {
                            popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(route = Screen.VolunteerDashboard.route) {
            VolunteerDashboardScreen(onNavigateToProfile = { navController.navigate(Screen.Profile.route) })
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


