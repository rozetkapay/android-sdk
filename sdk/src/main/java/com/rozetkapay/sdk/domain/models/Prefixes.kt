package com.rozetkapay.sdk.domain.models

internal interface PrefixContainable {
    fun hasEqualPrefix(string: String): Boolean
}

internal class PrefixInt(private val value: Int) : PrefixContainable {
    override fun hasEqualPrefix(string: String): Boolean {
        return string.startsWith(value.toString())
    }
}

internal class PrefixRange(private val range: IntRange) : PrefixContainable {
    override fun hasEqualPrefix(string: String): Boolean {
        return range.any { string.startsWith(it.toString()) }
    }
}