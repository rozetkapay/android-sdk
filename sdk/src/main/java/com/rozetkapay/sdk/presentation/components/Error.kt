package com.rozetkapay.sdk.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun ErrorScreen(
    message: String = stringResource(id = R.string.rozetka_pay_common_error_message),
    onRetry: () -> Unit,
    onCancel: () -> Unit,

    ) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, CenterVertically)
    ) {
        Column(
            modifier = Modifier
                .padding(
                    vertical = 24.dp,
                )
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, CenterVertically)
        ) {
            Image(
                modifier = Modifier
                    .size(200.dp),
                painter = painterResource(id = R.drawable.rozetka_pay_image_error),
                contentDescription = "error-image"
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = message,
                style = DomainTheme.typography.body,
                color = DomainTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )
        }
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rozetka_pay_common_button_cancel),
            onClick = onCancel
        )
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(id = R.string.rozetka_pay_common_button_retry),
            onClick = onRetry
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    uiMode = UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Night mode",
    showBackground = true,
    backgroundColor = 0xFF000000,
    uiMode = UI_MODE_NIGHT_YES,
)
private fun ErrorScreenPreview() {
    RozetkaPayTheme {
        ErrorScreen(
            onRetry = {},
            onCancel = {}
        )
    }
}
