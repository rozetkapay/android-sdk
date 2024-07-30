package com.rozetkapay.sdk.domain.validators.rules

import kotlinx.datetime.LocalDate

fun interface CardExpirationDateValidationRule {
    fun validate(
        currentDate: LocalDate,
        expYear: Int,
        expMonth: Int,
    ): Boolean
}

object DefaultCardExpirationDateValidationRule : CardExpirationDateValidationRule {
    override fun validate(currentDate: LocalDate, expYear: Int, expMonth: Int): Boolean {
        val currentYear: Int = currentDate.year % 100
        val currentMonth: Int = currentDate.monthNumber
        return expYear > currentYear || (expYear == currentYear && expMonth >= currentMonth)
    }
}

class MinimalDateCardExpirationDateValidationRule(
    private val allowedMinimalDate: LocalDate,
) : CardExpirationDateValidationRule {
    override fun validate(currentDate: LocalDate, expYear: Int, expMonth: Int): Boolean {
        val minYear: Int = allowedMinimalDate.year % 100
        val minMonth: Int = allowedMinimalDate.monthNumber
        return expYear > minYear || (expYear == minYear && expMonth >= minMonth)
    }
}