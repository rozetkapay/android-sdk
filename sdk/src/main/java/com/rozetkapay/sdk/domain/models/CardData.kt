package com.rozetkapay.sdk.domain.models

internal data class CardData(
    val number: String,
    val cvv: String,
    val expDate: CardExpDate,
    val cardholderName: String?
)

internal data class CardExpDate(
    val year: Int,
    val month: Int,
) {

    companion object {
        fun parse(rawDateString: String): CardExpDate {
            val filteredDate = rawDateString.filter { it.isDigit() }

            if (filteredDate.length != 4) {
                throw CardExpirationDateException.StringWrongLength()
            }

            val month = filteredDate.substring(0, 2).toIntOrNull()
            val year = filteredDate.substring(2, 4).toIntOrNull()

            if (month != null && year != null) {
                return CardExpDate(
                    year = year,
                    month = month
                )
            } else {
                throw CardExpirationDateException.ParsingError()
            }
        }
    }
}

internal sealed class CardExpirationDateException : Throwable() {
    class StringWrongLength : CardExpirationDateException()
    class ParsingError : CardExpirationDateException()
}