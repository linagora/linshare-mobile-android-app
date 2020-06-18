package com.linagora.android.linshare.util

import android.content.Context
import android.text.format.DateFormat
import androidx.core.os.ConfigurationCompat
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date

class TimeUtils(private val context: Context) {

    companion object {
        private const val LAST_LOGIN_FORMAT = "dd.MM.YYYY hh:mm a"

        private const val LAST_MODIFIED_FORMAT = "MMM dd, YYYY"
    }

    sealed class LinShareTimeFormat(val pattern: String) {
        object LastLoginFormat : LinShareTimeFormat(LAST_LOGIN_FORMAT)

        object LastModifiedFormat : LinShareTimeFormat(LAST_MODIFIED_FORMAT)
    }

    fun convertToLocalTime(date: Date, format: LinShareTimeFormat): String {
        val currentLocale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
        val lastModified = DateFormat.getBestDateTimePattern(currentLocale, format.pattern)
        val formatter = DateTimeFormatter.ofPattern(lastModified)
        return DateTimeUtils.toInstant(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)
    }
}
