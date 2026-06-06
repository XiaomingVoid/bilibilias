package com.imcys.bilibilias.common.utils

object NumberUtils {

    private const val WAN = 10000.0
    private const val YI = 100000000.0

    /**
     * Formats a large number into a more readable string with suffixes like '万' or '亿'.
     *
     * @param number The number to format.
     * @return A formatted string. For example, 12345 becomes "1.2万", 123456789 becomes "1.2亿".
     */
    fun formatLargeNumber(number: Long?): String {
        if (number == null) return "0"
        if (number < WAN) {
            return number.toString()
        }

        return if (number < YI) {
            formatValue(number / WAN, "万")
        } else {
            formatValue(number / YI, "亿")
        }
    }

    /**
     * Helper function to format the value to one decimal place if it's not a whole number.
     */
    private fun formatValue(value: Double, unit: String): String {
        val truncated = kotlin.math.floor(value * 10) / 10
        val isWholeNumber = truncated % 1.0 == 0.0
        val display = if (isWholeNumber) {
            truncated.toLong().toString()
        } else {
            truncated.toString().removeSuffix("0").removeSuffix(".")
        }
        return "$display$unit"
    }
}
