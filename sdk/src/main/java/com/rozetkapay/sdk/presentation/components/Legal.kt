package com.rozetkapay.sdk.presentation.components

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.sdk.R
import com.rozetkapay.sdk.domain.RozetkaPayConfig
import com.rozetkapay.sdk.presentation.theme.DomainTheme
import com.rozetkapay.sdk.presentation.theme.RozetkaPayTheme
import com.rozetkapay.sdk.presentation.util.withResourceId

@Composable
internal fun LegalIconsBlock(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .withResourceId("legalIconsBlock"),
        horizontalArrangement = Arrangement.spacedBy(
            space = 16.dp,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier
                .withResourceId("legalIconVisa")
                .height(20.dp),
            painter = painterResource(id = R.drawable.rozetka_pay_legal_visa),
            contentDescription = "legal-icon-visa"
        )
        Image(
            modifier = Modifier
                .withResourceId("legalIconMastercard")
                .height(20.dp),
            painter = painterResource(id = R.drawable.rozetka_pay_legal_mastercard),
            contentDescription = "legal-icon-pcidss"
        )
        Image(
            modifier = Modifier
                .withResourceId("legalIcoProstir")
                .height(20.dp),
            painter = painterResource(id = R.drawable.rozetka_pay_legal_prostir),
            contentDescription = "legal-icon-mastercard"
        )
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
internal fun LegalBlockPreview() {
    RozetkaPayTheme {
        LegalIconsBlock()
    }
}

@Composable
internal fun LegalTextBlock(
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier
            .withResourceId("legalTextBlock"),
        text = buildAnnotatedString {
            append(
                stringResource(
                    id = R.string.rozetka_pay_payment_legal_text_part_1,
                    stringResource(R.string.rozetka_pay_payment_legal_pay_button)
                )
            )
            withLink(
                LinkAnnotation.Url(
                    url = RozetkaPayConfig.LEGAL_PUBLIC_CONTRACT_LINK,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                )
            ) {
                append(stringResource(id = R.string.rozetka_pay_payment_legal_text_part_2))
            }
            append(stringResource(id = R.string.rozetka_pay_payment_legal_text_part_3))
            withLink(
                LinkAnnotation.Url(
                    url = RozetkaPayConfig.LEGAL_COMPANY_DETAILS_LINK,
                    styles = TextLinkStyles(
                        style = SpanStyle(
                            textDecoration = TextDecoration.Underline
                        )
                    )
                )
            ) {
                append(stringResource(id = R.string.rozetka_pay_payment_legal_text_part_4))
            }
            append(stringResource(id = R.string.rozetka_pay_payment_legal_text_part_5))
        },
        textAlign = TextAlign.Center,
        style = DomainTheme.typography.legalText,
        color = DomainTheme.colors.placeholder
    )
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Preview(uiMode = UI_MODE_NIGHT_YES)
internal fun LegalTextBlockPreview() {
    RozetkaPayTheme {
        LegalTextBlock()
    }
}