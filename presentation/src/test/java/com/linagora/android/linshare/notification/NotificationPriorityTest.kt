package com.linagora.android.linshare.notification

import android.os.Build
import androidx.core.app.NotificationCompat
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class NotificationPriorityTest {

    companion object {
        private val VALID_PRIORITIES = listOf(
            NotificationCompat.PRIORITY_MIN,
            NotificationCompat.PRIORITY_LOW,
            NotificationCompat.PRIORITY_DEFAULT,
            NotificationCompat.PRIORITY_HIGH,
            NotificationCompat.PRIORITY_MAX
        )

        private val INVALID_PRIORITIES = listOf(-10, -3, 3, 100)
    }

    @Test
    fun validateNotificationPriority() {
        for (priority in VALID_PRIORITIES) {
            try {
                NotificationPriority(priority)
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }

        for (invalid_priority in INVALID_PRIORITIES) {
            assertThrows<java.lang.IllegalArgumentException> {
                NotificationPriority(invalid_priority)
            }
        }
    }
}
