package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun Title(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier,
        text = title,
        color = DomainTheme.colors.title,
        style = DomainTheme.typography.title,
    )
}

@Composable
internal fun Subtitle(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier,
        text = title,
        color = DomainTheme.colors.subtitle,
        style = DomainTheme.typography.subtitle,
    )
}

@Composable
internal fun Label(
    modifier: Modifier = Modifier,
    title: String,
) {
    Text(
        modifier = modifier,
        text = title,
        color = DomainTheme.colors.onSurface,
        style = DomainTheme.typography.labelSmall,
    )
}

@Composable
@Preview(showBackground = true)
fun TitlesPreview() {
    RozetkaPayTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Title(title = "Title sample")
            Subtitle(title = "Subtitle sample")
            Label(title = "Label sample")
        }
    }
}
