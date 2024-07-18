package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun DevBadge(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(
                vertical = 6.dp,
                horizontal = 12.dp
            ),
        text = "DEV",
        style = MaterialTheme.typography.labelMedium
    )
}

@Composable
@Preview(showBackground = true)
private fun DevBadgePreview() {
    RozetkaPayTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            DevBadge()
        }
    }
}
