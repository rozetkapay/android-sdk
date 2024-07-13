package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

private const val LOADING_SCREEN_MIN_HEIGHT_DP = 160

@Composable
internal fun LoadingScreen(
    message: String = stringResource(id = R.string.rozetka_pay_common_loading_message),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = LOADING_SCREEN_MIN_HEIGHT_DP.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, CenterVertically)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(36.dp),
            strokeCap = StrokeCap.Round,
            color = DomainTheme.colorScheme.primary
        )
        Label(title = message)
    }
}

@Composable
@Preview(showBackground = true)
private fun LoadingScreenPreview() {
    RozetkaPayTheme {
        LoadingScreen(
            message = "Loading..."
        )
    }
}
