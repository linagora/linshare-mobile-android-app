package com.linagora.android.linshare.util

import com.linagora.android.linshare.util.FileSize.SizeFormat.LONG
import com.linagora.android.linshare.util.FileSize.SizeFormat.SHORT

data class FileSize(val value: Long) {

    init {
        require(value >= 0) { "Size must not be negative" }
    }

    private val THRESHOLD_ROUND_FILE_SIZE = 900

    enum class Unit(val value: String) {
        B("B"),
        KB("KB"),
        MB("MB"),
        GB("GB"),
        TB("TB"),
        PB("PB")
    }

    enum class SizeFormat {
        SHORT,
        LONG
    }

    fun format(sizeFormat: SizeFormat): String {
        var formattedSize = value / 1f
        val maxStep = 5
        var step = 0

        while (formattedSize > THRESHOLD_ROUND_FILE_SIZE && step < maxStep) {
            step += 1
            formattedSize /= 1000
        }

        val unit = Unit.values()[step]
        val roundFormat = when (sizeFormat) {
            SHORT -> "%.1f %s"
            LONG -> "%.2f %s"
        }
        return when (unit) {
            Unit.B -> String.format("%s %s", formattedSize.toInt(), unit)
            else -> String.format(roundFormat, formattedSize, unit)
        }
    }

    override fun toString() = format(SHORT)
}
