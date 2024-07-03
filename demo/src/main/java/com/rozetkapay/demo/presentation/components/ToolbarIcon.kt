package com.rozetkapay.demo.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun ToolbarIcon(
    painter: Painter,
    onClick: () -> Unit,
    contentDescription: String = "Toolbar icon",
) {
    Icon(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(8.dp),
        painter = painter,
        contentDescription = contentDescription,
        tint = MaterialTheme.colorScheme.onSurface
    )
}
