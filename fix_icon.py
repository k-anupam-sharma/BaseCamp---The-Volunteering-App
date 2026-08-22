import sys
import re

path = 'app/src/main/java/com/example/basecamp/presentation/screens/profile/ProfileScreen.kt'
content = open(path, 'r', encoding='utf-8').read()

content = content.replace("import androidx.compose.material.icons.automirrored.filled.ArrowBack", "import androidx.compose.material.icons.filled.ArrowBack")
content = content.replace("Icons.AutoMirrored.Filled.ArrowBack", "Icons.Filled.ArrowBack")

open(path, 'w', encoding='utf-8').write(content)
