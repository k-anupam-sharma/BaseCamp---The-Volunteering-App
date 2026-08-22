import sys
import re

def fix_screen(path):
    content = open(path, 'r', encoding='utf-8').read()
    
    if "import androidx.compose.foundation.verticalScroll" not in content:
        content = content.replace("import androidx.compose.foundation.layout.*", "import androidx.compose.foundation.layout.*\nimport androidx.compose.foundation.verticalScroll\nimport androidx.compose.foundation.rememberScrollState")
        
    content = re.sub(r'(\.fillMaxSize\(\))', r'\1\n            .verticalScroll(rememberScrollState())', content)
    open(path, 'w', encoding='utf-8').write(content)

fix_screen('app/src/main/java/com/example/basecamp/presentation/auth/LoginScreen.kt')
fix_screen('app/src/main/java/com/example/basecamp/presentation/auth/SignupScreen.kt')
