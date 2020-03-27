package com.linagora.android.linshare.notification

import android.app.NotificationManager
import android.os.Build
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class NotificationImportanceTest {

    companion object {
        private val VALID_IMPORTANCES = listOf(
            NotificationManager.IMPORTANCE_MIN,
            NotificationManager.IMPORTANCE_LOW,
            NotificationManager.IMPORTANCE_DEFAULT,
            NotificationManager.IMPORTANCE_HIGH,
            NotificationManager.IMPORTANCE_MAX
        )

        private val INVALID_IMPORTANCES = listOf(-1, 0, 6, 100, 1000)
    }

    @Test
    fun validateNotificationImportance() {
        for (importance in VALID_IMPORTANCES) {
            try {
                NotificationImportance(importance)
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }

        for (invalid_importance in INVALID_IMPORTANCES) {
            assertThrows<IllegalArgumentException> {
                NotificationImportance(invalid_importance)
            }
        }
    }
}
