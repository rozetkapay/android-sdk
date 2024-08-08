package com.rozetkapay.sdk.util

import android.util.SparseArray
import androidx.core.util.getOrElse
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

object MoneyFormatter {
    const val DECIMAL_SEPARATOR = '.'
    private const val DEFAULT_EXPONENT = 2
    private val symbols = DecimalFormatSymbols.getInstance().apply {
        groupingSeparator = ' '
        decimalSeparator = DECIMAL_SEPARATOR
    }

    private val formats: SparseArray<DecimalFormat> = SparseArray()

    @JvmOverloads
    fun formatCoinsToMoney(coins: BigDecimal, currency: String? = null, exponent: Int = DEFAULT_EXPONENT): String {
        return formatMoney(coins.multiply(getCoin(exponent)), currency, exponent)
    }

    @JvmOverloads
    fun formatCoinsToMoney(coins: Long, currency: String? = null, exponent: Int = DEFAULT_EXPONENT): String {
        return formatCoinsToMoney(BigDecimal.valueOf(coins), currency, exponent)
    }

    @JvmOverloads
    fun formatMoney(money: BigDecimal, currency: String? = null, exponent: Int = DEFAULT_EXPONENT): String {
        val sign = if (money < BigDecimal.ZERO) "-" else ""
        val currencySuffix = if (currency == null) {
            ""
        } else {
            " $currency"
        }
        val formattedMoney = getFormat(exponent).format(money.abs())
        return "$sign$formattedMoney$currencySuffix"
    }

    private fun getCoin(exponent: Int): BigDecimal {
        if (exponent < 0) throw IllegalArgumentException("Exponent can't be less than zero")
        val divisor = BigDecimal(10).pow(exponent)
        return BigDecimal.ONE.divide(divisor)
    }

    private fun getFormat(exponent: Int): DecimalFormat {
        if (exponent < 0) throw IllegalArgumentException("Exponent can't be less than zero")
        return formats.getOrElse(exponent) {
            val format = generateFormat(exponent)
            formats.append(exponent, format)
            format
        }
    }

    private fun generateFormat(exponent: Int): DecimalFormat {
        return if (exponent == 0) {
            DecimalFormat("###,##0", symbols)
        } else {
            DecimalFormat("###,##0." + "0".repeat(exponent), symbols)
        }
    }
}