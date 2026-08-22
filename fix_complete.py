import sys
import re

path = 'app/src/main/java/com/example/basecamp/presentation/auth/CompleteProfileScreen.kt'
content = open(path, 'r', encoding='utf-8').read()

# Remove organizationName usage
content = content.replace('    var organizationName by remember { mutableStateOf("") }\n', '')

org_fields = """            BrutalistTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = "ORGANIZATION NAME"
            )
            Spacer(modifier = Modifier.height(16.dp))
            BrutalistTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = "PHONE NUMBER"
            )
            Spacer(modifier = Modifier.height(16.dp))
            BrutalistTextField(
                value = website,
                onValueChange = { website = it },
                placeholder = "WEBSITE (OPTIONAL)"
            )"""

content = re.sub(r'            BrutalistTextField\(\n                value = organizationName,.*?placeholder = "WEBSITE \(OPTIONAL\)"\n            \)', org_fields, content, flags=re.DOTALL)

content = content.replace(
    'organizationName = organizationName,\n',
    ''
)

open(path, 'w', encoding='utf-8').write(content)
