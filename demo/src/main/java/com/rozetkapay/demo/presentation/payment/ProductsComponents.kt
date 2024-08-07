package com.rozetkapay.demo.presentation.payment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rozetkapay.demo.R
import com.rozetkapay.demo.domain.models.Product
import com.rozetkapay.demo.presentation.theme.RozetkaPayDemoTheme
import java.math.BigDecimal
import java.math.RoundingMode

data class CartItemData(
    val product: Product,
    val count: Int,
)

@Composable
fun CartItem(item: CartItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(6.dp),
            model = item.product.imageUrl,
            contentDescription = "product-image",
            placeholder = painterResource(id = R.drawable.product_placeholder),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .align(CenterVertically)
        ) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = item.product.name
            )
            Text(
                style = MaterialTheme.typography.labelSmall,
                text = "${item.count} x ${item.product.price.amount()}"
            )
        }
        Text(
            modifier = Modifier.padding(top = 4.dp),
            text = (item.product.price * item.count).amount(),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

fun Long.amount(): String {
    return this.toBigDecimal()
        .divide(BigDecimal(100))
        .setScale(2, RoundingMode.HALF_UP)
        .toPlainString()
}

@Preview(
    showBackground = true
)
@Composable
private fun CartItemPreview() {
    RozetkaPayDemoTheme {
        CartItem(
            item = PaymentViewModel.mockedCartItemData.first()
        )
    }
}