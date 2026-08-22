package com.example.basecamp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.basecamp.presentation.theme.brutalistStyle

@Composable
fun BrutalistButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color(0xFFFAFF00), // Electric Yellow
    textColor: Color = Color.Black,
    cornerRadius: Dp = 0.dp // Sharp edges by default, can be adjusted
) {
    Box(
        modifier = modifier
            .brutalistStyle(cornerRadius = cornerRadius)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontWeight = FontWeight.ExtraBold, // Geometric and bold
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun BrutalistCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    cornerRadius: Dp = 0.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .brutalistStyle(cornerRadius = cornerRadius)
            .background(color = backgroundColor, shape = RoundedCornerShape(cornerRadius))
            .padding(contentPadding)
    ) {
        content()
    }
}

@Composable
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
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
                innerTextField()
            }
        }
    )
}



