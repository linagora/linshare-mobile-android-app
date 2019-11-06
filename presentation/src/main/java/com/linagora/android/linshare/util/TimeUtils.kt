package com.linagora.android.linshare.util

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.util.Date

object TimeUtils {

    private const val LAST_LOGIN_FORMAT = "dd.MM.YYYY hh:mm a"

    fun convertToLocalTime(date: Date): String {
        val formatter = DateTimeFormatter.ofPattern(LAST_LOGIN_FORMAT)
        return DateTimeUtils.toInstant(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
            .format(formatter)
    }
}
