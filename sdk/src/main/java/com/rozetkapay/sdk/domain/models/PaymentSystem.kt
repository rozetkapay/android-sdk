package com.rozetkapay.sdk.domain.models

internal enum class PaymentSystem(
    val alias: String,
    val prefixes: List<PrefixContainable>,
) {
    Visa(
        alias = "visa",
        prefixes = listOf(
            PrefixInt(4)
        )
    ),
    MasterCard(
        alias = "mastercard",
        prefixes = listOf(
            PrefixRange(51..55),
            PrefixRange(2221..2720),
            PrefixInt(5018),
            PrefixInt(5020),
            PrefixInt(5038),
            PrefixInt(5893),
            PrefixInt(6304),
            PrefixInt(6759),
            PrefixInt(6761),
            PrefixInt(6762),
            PrefixInt(6763)
        )
    ),
    Prostir(
        alias = "prostir",
        prefixes = listOf(
            PrefixInt(9)
        )
    )
}
