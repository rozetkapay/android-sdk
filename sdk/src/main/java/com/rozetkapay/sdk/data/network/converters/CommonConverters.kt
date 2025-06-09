package com.rozetkapay.sdk.data.network.converters

internal fun Long.toAmountDto(): Double =
    this.toBigDecimal().divide(100.toBigDecimal()).toDouble()