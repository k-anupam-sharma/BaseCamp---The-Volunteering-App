import sys
import re

path = 'app/src/main/java/com/example/basecamp/presentation/auth/AuthViewModel.kt'
content = open(path, 'r', encoding='utf-8').read()

content = content.replace(
    'fun completeGoogleSignup(userId: String, email: String, name: String, role: String, organizationName: String, phone: String, website: String)',
    'fun completeGoogleSignup(userId: String, email: String, name: String, role: String, phone: String, website: String)'
)

content = content.replace(
    'organizationName = if (role == "Organization") organizationName else null,\n',
    ''
)

open(path, 'w', encoding='utf-8').write(content)
