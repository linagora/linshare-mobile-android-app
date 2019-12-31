package com.linagora.android.linshare.util

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date

object TimeUtils {

    private const val LAST_LOGIN_FORMAT = "dd.MM.YYYY hh:mm a"

    private const val LAST_MODIFIED_FORMAT = "dd MMM YYYY HH:mm"

    sealed class LinShareTimeFormat(val pattern: String) {
        object LastLoginFormat : LinShareTimeFormat(LAST_LOGIN_FORMAT)

        object LastModifiedFormat : LinShareTimeFormat(LAST_MODIFIED_FORMAT)
    }

    fun convertToLocalTime(date: Date, format: LinShareTimeFormat): String {
        val formatter = DateTimeFormatter.ofPattern(format.pattern)
        return DateTimeUtils.toInstant(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)
    }
}
