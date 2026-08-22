import sys
content = open('app/src/main/java/com/example/basecamp/presentation/components/Components.kt', 'r', encoding='utf-8').read()
content = content.replace("import androidx.compose.foundation.layout.Box", "import androidx.compose.foundation.layout.Box\nimport androidx.compose.foundation.layout.Row\nimport androidx.compose.foundation.layout.size")
open('app/src/main/java/com/example/basecamp/presentation/components/Components.kt', 'w', encoding='utf-8').write(content)
