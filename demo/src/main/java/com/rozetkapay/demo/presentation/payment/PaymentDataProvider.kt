package com.rozetkapay.demo.presentation.payment

import com.rozetkapay.demo.domain.models.Product

object PaymentDataProvider {
    val products1 = Product(
        name = "RZTK Buds TWS Black",
        imageUrl = "https://content1.rozetka.com.ua/goods/images/big/378870632.jpg",
        price = 62900L
    )
    val products2 = Product(
        name = "Smartphone Case",
        imageUrl = "https://content1.rozetka.com.ua/goods/images/big/379377051.jpg",
        price = 22900L
    )
    val products3 = Product(
        name = "RZTK Power Bank",
        imageUrl = "https://content1.rozetka.com.ua/goods/images/big/426602978.jpg",
        price = 59900L
    )

    val products4 = Product(
        name = "RZTK Plasma 20W RGB",
        imageUrl = "https://content1.rozetka.com.ua/goods/images/big/392327478.jpg",
        price = 119900L
    )

    val cartItems = listOf(
        CartItemData(
            product = products1,
            count = 1
        ),
        CartItemData(
            product = products2,
            count = 2
        ),
        CartItemData(
            product = products3,
            count = 1
        ),
        CartItemData(
            product = products4,
            count = 1
        ),
    )

    val groupedCartItems = listOf(
        GroupedCartItemsData(
            title = "Rozetka",
            items = listOf(
                CartItemData(
                    product = products1,
                    count = 1
                ),
                CartItemData(
                    product = products2,
                    count = 2
                ),
                CartItemData(
                    product = products3,
                    count = 1
                ),
            )
        ),
        GroupedCartItemsData(
            title = "Rozetka EU+",
            items = listOf(
                CartItemData(
                    product = products4,
                    count = 1
                )
            )
        )
    )
}