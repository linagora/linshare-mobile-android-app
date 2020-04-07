package com.linagora.android.linshare.utils

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastLoginFormat
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.testshared.TestFixtures.Accounts.LAST_LOGIN_DATE
import org.junit.jupiter.api.Test
import java.time.format.DateTimeFormatter

class TimeUtilsTest {

    @Test
    fun convertToLocalTimeShouldFollowLastLoginFormat() {
        val dateTime = TimeUtils.convertToLocalTime(LAST_LOGIN_DATE, LastLoginFormat)

        assertThat(isValidFormat("dd.MM.YYYY hh:mm a", dateTime)).isTrue()
    }

    @Test
    fun convertToLocalTimeShouldFollowLastModifiedFormat() {
        val dateTime = TimeUtils.convertToLocalTime(LAST_LOGIN_DATE, LastModifiedFormat)

        assertThat(isValidFormat("MMM dd, YYYY", dateTime)).isTrue()
    }

    private fun isValidFormat(format: String, value: String): Boolean {
        return runCatching {
            val formatter = DateTimeFormatter.ofPattern(format)
            val test = formatter.parse(value)
            return formatter.format(test) == value
        }.getOrDefault(false)
    }
}
