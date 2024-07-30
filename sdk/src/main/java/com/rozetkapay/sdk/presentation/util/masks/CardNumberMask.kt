package com.rozetkapay.sdk.presentation.util.masks

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

internal class CardNumberMask(
    private val separator: String = " ",
) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return makeCardNumberFilter(text, separator)
    }

    private fun makeCardNumberFilter(
        text: AnnotatedString,
        separator: String,
    ): TransformedText {
        val trimmed = if (text.text.length >= MAX_CREDIT_CARD_NUMBER_LENGTH) {
            text.text.substring(0..<MAX_CREDIT_CARD_NUMBER_LENGTH)
        } else {
            text.text
        }
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 3 || i == 7 || i == 11) out += separator
            if (i == 15 && trimmed.length > 16) out += separator
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return when {
                    offset <= 3 -> offset
                    offset <= 7 -> offset + 1
                    offset <= 11 -> offset + 2
                    offset <= 16 -> offset + 3
                    offset <= 19 -> offset + 4
                    else -> 23
                }
            }

            override fun transformedToOriginal(offset: Int): Int {
                return when {
                    offset <= 4 -> offset
                    offset <= 9 -> offset - 1
                    offset <= 14 -> offset - 2
                    offset <= 19 -> offset - 3
                    offset <= 23 -> offset - 4
                    else -> 19
                }
            }
        }

        return TransformedText(AnnotatedString(out), offsetMapping)
    }

    companion object {
        internal const val MAX_CREDIT_CARD_NUMBER_LENGTH = 19
    }
}