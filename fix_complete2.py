import sys
import re

path = 'app/src/main/java/com/example/basecamp/presentation/auth/CompleteProfileScreen.kt'
content = open(path, 'r', encoding='utf-8').read()

# Replace RoleSelectionTab with inline implementation
inline_role = """        // Role Selection
        Text(
            text = "SELECT YOUR ROLE:",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val volColor = if (role == "Volunteer") Color(0xFFFAFF00) else Color.White
            val orgColor = if (role == "Organization") Color(0xFFFAFF00) else Color.White

            BrutalistButton(
                text = "VOLUNTEER",
                onClick = { role = "Volunteer" },
                backgroundColor = volColor,
                modifier = Modifier.weight(1f)
            )
            BrutalistButton(
                text = "ORGANIZATION",
                onClick = { role = "Organization" },
                backgroundColor = orgColor,
                modifier = Modifier.weight(1f)
            )
        }"""

content = re.sub(r'        RoleSelectionTab\(.*?\n        \)', inline_role, content, flags=re.DOTALL)
content = content.replace("import com.example.basecamp.presentation.components.RoleSelectionTab", "")

open(path, 'w', encoding='utf-8').write(content)
