package com.linagora.android.linshare.utils

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.TimeUtils
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastLoginFormat
import com.linagora.android.linshare.util.TimeUtils.LinShareTimeFormat.LastModifiedFormat
import com.linagora.android.testshared.TestFixtures.Accounts.LAST_LOGIN_DATE
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.format.DateTimeFormatter

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class TimeUtilsTest {

    lateinit var context: Context

    private lateinit var timeUtils: TimeUtils

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        timeUtils = TimeUtils(context)
    }

    @Test
    fun convertToLocalTimeShouldFollowLastLoginFormat() {
        val dateTime = timeUtils.convertToLocalTime(LAST_LOGIN_DATE, LastLoginFormat)

        assertThat(isValidFormat("dd.MM.YYYY hh:mm a", dateTime)).isTrue()
    }

    @Test
    fun convertToLocalTimeShouldFollowLastModifiedFormat() {
        val dateTime = timeUtils.convertToLocalTime(LAST_LOGIN_DATE, LastModifiedFormat)

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
