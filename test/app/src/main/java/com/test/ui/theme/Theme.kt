package com.test.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        content = content
    )
}