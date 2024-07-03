package com.rozetkapay.demo.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp

@Composable
fun SimpleToolbar(
    title: String,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            ToolbarTitle(
                modifier = Modifier.padding(end = 48.dp),
                title = title
            )
        },
        navigationIcon = {
            ToolbarIcon(
                painter = rememberVectorPainter(Icons.Default.KeyboardArrowLeft),
                onClick = onBack
            )
        }
    )
}