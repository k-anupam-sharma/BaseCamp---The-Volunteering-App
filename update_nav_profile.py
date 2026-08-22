import sys
import re

path = 'app/src/main/java/com/example/basecamp/presentation/navigation/NavGraph.kt'
content = open(path, 'r', encoding='utf-8').read()

if "import com.example.basecamp.presentation.screens.profile.ProfileScreen" not in content:
    content = content.replace("import com.example.basecamp.presentation.auth.CompleteProfileScreen", "import com.example.basecamp.presentation.auth.CompleteProfileScreen\nimport com.example.basecamp.presentation.screens.profile.ProfileScreen")

profile_route = """        composable(route = Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
"""

if "ProfileScreen(" not in content:
    content = content.replace("        composable(route = Screen.ScanTicket.route)", profile_route + "\n        composable(route = Screen.ScanTicket.route)")

# Also update the Dashboard routing to pass onNavigateToProfile
if "VolunteerDashboardScreen()" in content:
    content = content.replace("VolunteerDashboardScreen()", "VolunteerDashboardScreen(onNavigateToProfile = { navController.navigate(Screen.Profile.route) })")
    
if "OrgDashboardScreen()" in content:
    content = content.replace("OrgDashboardScreen()", "OrgDashboardScreen(onNavigateToProfile = { navController.navigate(Screen.Profile.route) })")

open(path, 'w', encoding='utf-8').write(content)
