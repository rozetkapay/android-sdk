package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    BarButton(
        modifier = modifier,
        painter = painterResource(id = R.drawable.ic_close),
        contentDescription = "button-close",
        onClick = onClick
    )
}

@Composable
internal fun BarButton(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Icon(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    radius = 16.dp,
                    bounded = false
                ),
                onClick = onClick,
            )
            .padding(4.dp)
            .size(20.dp)
            .clip(shape = CircleShape),
        painter = painter,
        contentDescription = contentDescription,
        tint = DomainTheme.colors.appBarIcon
    )
}

@Composable
@Preview(showBackground = true)
private fun BarButtonsPreview() {
    RozetkaPayTheme {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            CloseButton(
                onClick = {}
            )
        }
    }
}
