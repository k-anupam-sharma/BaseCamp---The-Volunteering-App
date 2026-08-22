import sys
import re

path = 'app/src/main/java/com/example/basecamp/presentation/navigation/NavGraph.kt'
content = open(path, 'r', encoding='utf-8').read()

content = content.replace(
    'val email = backStackEntry.arguments?.getString("email") ?: ""',
    'val email = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("email") ?: "", "UTF-8")'
)
content = content.replace(
    'val name = backStackEntry.arguments?.getString("name") ?: ""',
    'val name = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("name") ?: "", "UTF-8")'
)

open(path, 'w', encoding='utf-8').write(content)
