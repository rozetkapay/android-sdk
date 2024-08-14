package com.rozetkapay.sdk.domain.models

internal enum class Currency(
    val codes: List<String>,
    val symbol: String,
) {
    UAH(
        codes = listOf("UAH"),
        symbol = "₴"
    ),
    USD(
        codes = listOf("USD"),
        symbol = "$"
    ),
    EUR(
        codes = listOf("EUR"),
        symbol = "€"
    ),
    GBP(
        codes = listOf("GBP"),
        symbol = "£"
    ),
    PLN(
        codes = listOf("PLN"),
        symbol = "zł"
    ),
    CHF(
        codes = listOf("CHF"),
        symbol = "₣"
    )
    ;

    companion object {
        private val codesMap: HashMap<String, Currency> by lazy {
            entries
                .fold(HashMap()) { map, currency ->
                    currency.codes.forEach { code -> map[code.uppercase()] = currency }
                    map
                }
        }

        fun getSymbol(currencyCode: String?): String {
            return getCurrency(currencyCode)?.symbol ?: currencyCode.orEmpty()
        }

        fun getCurrency(currencyCode: String?): Currency? {
            return if (currencyCode != null) {
                return codesMap[currencyCode.uppercase()]
            } else {
                null
            }
        }
    }
}