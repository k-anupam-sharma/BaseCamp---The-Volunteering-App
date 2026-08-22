import sys
import re

def update_dashboard(path, title):
    content = open(path, 'r', encoding='utf-8').read()
    
    # Signature
    if "onNavigateToProfile: () -> Unit =" not in content:
        content = re.sub(r'(fun (Volunteer|Org)DashboardScreen\(\n)', r'\1    onNavigateToProfile: () -> Unit = {},\n', content)
        
    # Imports
    if "import androidx.compose.material.icons.Icons" not in content:
        content = content.replace("import androidx.compose.material3.Text", "import androidx.compose.material3.Text\nimport androidx.compose.material3.Icon\nimport androidx.compose.material3.IconButton\nimport androidx.compose.material.icons.Icons\nimport androidx.compose.material.icons.filled.Person")

    # Layout
    old_title = f"""        Text(
            text = "{title}",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 24.dp, top = 16.dp)
        )"""
    
    new_title = f"""        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp, top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {{
            Text(
                text = "{title}",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                letterSpacing = 1.sp
            )
            IconButton(onClick = onNavigateToProfile) {{
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Profile",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp)
                )
            }}
        }}"""
    
    content = content.replace(old_title, new_title)
    
    open(path, 'w', encoding='utf-8').write(content)

update_dashboard('app/src/main/java/com/example/basecamp/presentation/screens/volunteer/VolunteerDashboardScreen.kt', 'EVENT FEED')
update_dashboard('app/src/main/java/com/example/basecamp/presentation/screens/organization/OrgDashboardScreen.kt', 'ORGANIZATION HUB')
