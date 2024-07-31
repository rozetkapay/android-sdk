package com.rozetkapay.sdk.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme

@Composable
internal fun LegalBlock(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
    ) {
        Image(
            modifier = Modifier
                .width(52.dp)
                .height(20.dp),
            painter = painterResource(id = R.drawable.rozetka_pay_legal_visa),
            contentDescription = "legal-icon-visa"
        )
        Image(
            modifier = Modifier
                .width(52.dp)
                .height(20.dp),
            painter = painterResource(id = R.drawable.rozetka_pay_legal_pcidss),
            contentDescription = "legal-icon-pcidss"
        )
        Image(
            modifier = Modifier
                .width(52.dp)
                .height(20.dp),
            painter = painterResource(id = R.drawable.rozetka_pay_legal_mastercard),
            contentDescription = "legal-icon-mastercard"
        )
    }
}

@Composable
@Preview
internal fun LegalBlockPreview() {
    RozetkaPayTheme {
        LegalBlock()
    }
}