package com.linagora.android.linshare.notification

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class SystemNotifierTest {

    @Test
    fun generateNotificationIdShouldBeInSystem() {
        val notifier = SystemNotifier()
        val notificationId = notifier.generateNotificationId()

        assertThat(notifier.getSystemNotifications())
            .containsExactly(notificationId)
    }

    @Test
    fun generateNotificationIdShouldAddNewItemInSystem() {
        val notifier = SystemNotifier()
        val notification1 = notifier.generateNotificationId()
        val notification2 = notifier.generateNotificationId()

        assertThat(notifier.getSystemNotifications())
            .containsExactly(notification1, notification2)
    }
}
