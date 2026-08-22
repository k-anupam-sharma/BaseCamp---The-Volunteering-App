import sys

path = 'app/src/main/java/com/example/basecamp/presentation/navigation/NavGraph.kt'
content = open(path, 'r', encoding='utf-8').read()

import re

# Add Screen.CompleteProfile
if "object CompleteProfile" not in content:
    content = content.replace(
        "object ScanTicket : Screen(\"scan_ticket\")",
        "object ScanTicket : Screen(\"scan_ticket\")\n    object CompleteProfile : Screen(\"complete_profile/{userId}/{email}/{name}\") {\n        fun createRoute(userId: String, email: String, name: String) = \"complete_profile/$userId/$email/$name\"\n    }\n    object Profile : Screen(\"profile\")"
    )

# Add import
if "import com.example.basecamp.presentation.auth.CompleteProfileScreen" not in content:
    content = content.replace("import com.example.basecamp.presentation.auth.SignupScreen", "import com.example.basecamp.presentation.auth.SignupScreen\nimport com.example.basecamp.presentation.auth.CompleteProfileScreen")

if "import androidx.navigation.NavType" not in content:
    content = content.replace("import androidx.navigation.compose.rememberNavController", "import androidx.navigation.compose.rememberNavController\nimport androidx.navigation.navArgument\nimport androidx.navigation.NavType")

# Add CompleteProfile screen to NavHost
complete_profile_route = """        composable(
            route = Screen.CompleteProfile.route,
            arguments = listOf(
                navArgument("userId") { type = NavType.StringType },
                navArgument("email") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val name = backStackEntry.arguments?.getString("name") ?: ""
            
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
"""

if "CompleteProfileScreen(" not in content:
    content = content.replace("        composable(route = Screen.VolunteerDashboard.route)", complete_profile_route + "\n        composable(route = Screen.VolunteerDashboard.route)")

# Update LoginScreen routing
login_update = """                onNeedsProfileSetup = { userId, email, name ->
                    val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                    navController.navigate(Screen.CompleteProfile.createRoute(userId, encodedEmail, encodedName))
                },"""
content = re.sub(r'LoginScreen\(\n\s+onNavigateToSignup', "LoginScreen(\n" + login_update + "\n                onNavigateToSignup", content)

signup_update = """                onNeedsProfileSetup = { userId, email, name ->
                    val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                    val encodedName = java.net.URLEncoder.encode(name, "UTF-8")
                    navController.navigate(Screen.CompleteProfile.createRoute(userId, encodedEmail, encodedName))
                },"""
content = re.sub(r'SignupScreen\(\n\s+onNavigateToLogin', "SignupScreen(\n" + signup_update + "\n                onNavigateToLogin", content)

open(path, 'w', encoding='utf-8').write(content)
