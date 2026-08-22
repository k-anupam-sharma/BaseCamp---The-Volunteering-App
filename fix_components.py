import sys

content = open('app/src/main/java/com/example/basecamp/presentation/components/Components.kt', 'r', encoding='utf-8').read()

new_imports = """import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
"""

if "import androidx.compose.material.icons.Icons" not in content:
    content = content.replace("import androidx.compose.foundation.background", new_imports + "import androidx.compose.foundation.background")

new_textfield = """@Composable
fun BrutalistTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    backgroundColor: Color = Color.White,
    cornerRadius: Dp = 0.dp,
    readOnly: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }
    
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        modifier = modifier
            .fillMaxWidth()
            .brutalistStyle(cornerRadius = cornerRadius)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .padding(16.dp),
        textStyle = TextStyle(
            color = Color.Black,
            fontWeight = FontWeight.Medium
        ),
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        innerTextField()
                    }
                    if (isPassword) {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.padding(start = 8.dp).size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }
        }
    )
}"""

import re
content = re.sub(r'@Composable\s+fun BrutalistTextField.*?\}\s*\)', new_textfield + "\n}", content, flags=re.DOTALL)

open('app/src/main/java/com/example/basecamp/presentation/components/Components.kt', 'w', encoding='utf-8').write(content)
