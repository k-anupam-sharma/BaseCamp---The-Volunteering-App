import sys

path = 'app/src/main/java/com/example/basecamp/presentation/navigation/NavGraph.kt'
content = open(path, 'r', encoding='utf-8').read()

profile_route = """
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
"""

if "ProfileScreen(" not in content:
    content = content.replace("    }\n}", profile_route + "    }\n}")

# Also let's fix OrgDashboardScreen missing onNavigateToProfile
if "OrgDashboardScreen(" in content and "onNavigateToProfile" not in content:
    content = content.replace(
        "            OrgDashboardScreen(\n                onNavigateToScan = { navController.navigate(Screen.ScanTicket.route) }\n            )",
        "            OrgDashboardScreen(\n                onNavigateToScan = { navController.navigate(Screen.ScanTicket.route) },\n                onNavigateToProfile = { navController.navigate(Screen.Profile.route) }\n            )"
    )

open(path, 'w', encoding='utf-8').write(content)
