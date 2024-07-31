package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = DomainTheme.colors.primary,
            contentColor = DomainTheme.colors.onPrimary,
            disabledContentColor = DomainTheme.colors.onPrimary.copy(alpha = 0.5f),
            disabledContainerColor = DomainTheme.colors.primary.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(DomainTheme.sizes.buttonCornerRadius)
    ) {
        Text(
            text = text,
            color = DomainTheme.colors.onPrimary,
            style = DomainTheme.typography.labelLarge
        )
    }
}

@Composable
internal fun TextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DomainTheme.colors.primary,
        ),
        border = null,
        shape = RoundedCornerShape(DomainTheme.sizes.buttonCornerRadius)
    ) {
        Text(
            text = text,
            color = DomainTheme.colors.primary,
            style = DomainTheme.typography.labelLarge
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun ButtonsPreview() {
    RozetkaPayTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            PrimaryButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Button",
                onClick = {}
            )
            TextButton(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Button",
                onClick = {}
            )
        }
    }
}