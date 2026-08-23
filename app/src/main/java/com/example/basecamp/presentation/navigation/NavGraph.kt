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
import com.example.basecamp.presentation.screens.organization.CreateEventScreen
import com.example.basecamp.presentation.screens.volunteer.VolunteerDashboardScreen
import com.example.basecamp.presentation.screens.volunteer.EventDetailsScreen
import com.example.basecamp.presentation.screens.organization.OrgEventDetailsScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Signup : Screen("signup")
    object VolunteerDashboard : Screen("volunteer_dashboard")
    object OrgDashboard : Screen("org_dashboard")
    object ScanTicket : Screen("scan_ticket")
    object CreateEvent : Screen("create_event")
    object EventDetails : Screen("event_details/{eventId}") {
        fun createRoute(eventId: String) = "event_details/$eventId"
    }
    object OrgEventDetails : Screen("org_event_details/{eventId}") {
        fun createRoute(eventId: String) = "org_event_details/$eventId"
    }
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
                    val safeEmail = email.ifEmpty { "no-email" }
                    val safeName = name.ifEmpty { "User" }
                    val encodedEmail = java.net.URLEncoder.encode(safeEmail, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(safeName, "UTF-8")
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
                    val safeEmail = email.ifEmpty { "no-email" }
                    val safeName = name.ifEmpty { "User" }
                    val encodedEmail = java.net.URLEncoder.encode(safeEmail, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(safeName, "UTF-8")
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
            VolunteerDashboardScreen(
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToEventDetails = { eventId -> navController.navigate(Screen.EventDetails.createRoute(eventId)) }
            )
        }

        composable(
            route = Screen.EventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailsScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.OrgDashboard.route) {
            OrgDashboardScreen(
                onNavigateToScan = { navController.navigate(Screen.ScanTicket.route) },
                onNavigateToCreate = { navController.navigate(Screen.CreateEvent.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToEventDetails = { eventId -> navController.navigate(Screen.OrgEventDetails.createRoute(eventId)) }
            )
        }
        
        composable(
            route = Screen.OrgEventDetails.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            OrgEventDetailsScreen(
                eventId = eventId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.CreateEvent.route) {
            CreateEventScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(route = Screen.ScanTicket.route) {
            com.example.basecamp.presentation.screens.organization.ScanTicketScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}


