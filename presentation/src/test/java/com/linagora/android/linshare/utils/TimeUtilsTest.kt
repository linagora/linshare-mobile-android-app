package com.linagora.android.linshare.utils

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.testshared.TestFixtures.Accounts.LAST_LOGIN_DATE
import org.junit.jupiter.api.Test
import java.time.format.DateTimeFormatter

class TimeUtilsTest {

    @Test
    fun convertToLocalTimeShouldFollowTheFormat() {
        val dateTime = TimeUtils.convertToLocalTime(LAST_LOGIN_DATE)

        assertThat(isValidFormat("dd.MM.YYYY hh:mm a", dateTime)).isTrue()
    }

    private fun isValidFormat(format: String, value: String): Boolean {
        return runCatching {
            val formatter = DateTimeFormatter.ofPattern(format)
            val test = formatter.parse(value)
            return formatter.format(test) == value
        }.getOrDefault(false)
    }
}
