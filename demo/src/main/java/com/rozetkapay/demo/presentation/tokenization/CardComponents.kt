package com.rozetkapay.demo.presentation.tokenization

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rozetkapay.demo.domain.models.CardToken
import com.rozetkapay.demo.domain.models.PaymentSystem
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme

@Composable
fun Card(
    modifier: Modifier = Modifier,
    card: CardToken,
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .width(30.dp)
                    .aspectRatio(10 / 6f),
                painter = card.paymentSystem.icon,
                contentDescription = "payment-system"
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = card.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = card.maskedNumber,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

val PaymentSystem.icon: Painter
    @Composable
    get() = painterResource(
        id = when (this) {
            PaymentSystem.Visa -> com.rozetkapay.sdk.R.drawable.ic_visa
            PaymentSystem.MasterCard -> com.rozetkapay.sdk.R.drawable.ic_mastercard
            PaymentSystem.Maestro -> com.rozetkapay.sdk.R.drawable.ic_maestro
            PaymentSystem.Prostir -> com.rozetkapay.sdk.R.drawable.ic_prostir
            is PaymentSystem.Other -> com.rozetkapay.sdk.R.drawable.ic_card_other
        }
    )

@Composable
@Preview
private fun CardPreview() {
    RozetkaPayDemoTheme {
        Card(
            card = CardsViewModel.mockedCards.first()
        )
    }
}

@Composable
fun CardsList(
    modifier: Modifier = Modifier,
    cards: List<CardToken>,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cards.forEach { card ->
            Card(
                card = card
            )
        }
    }
}

@Composable
@Preview
private fun CardsListPreview() {
    RozetkaPayDemoTheme {
        CardsList(
            cards = CardsViewModel.mockedCards
        )
    }
}
