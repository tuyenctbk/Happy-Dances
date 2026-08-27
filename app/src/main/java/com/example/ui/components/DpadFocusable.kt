package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.dpadFocusable(
    shape: Shape = RoundedCornerShape(16.dp),
    focusColor: Color = Color(0xFFFFD54F),
    borderWidth: Dp = 3.dp,
    enabled: Boolean = true
): Modifier {
    if (!enabled) return this
    var isFocused by remember { mutableStateOf(false) }
    return this
        .onFocusChanged { isFocused = it.isFocused }
        .focusable()
        .then(
            if (isFocused) {
                Modifier.border(borderWidth, focusColor, shape)
            } else {
                Modifier
            }
        )
}
