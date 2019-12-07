package com.linagora.android.linshare.notification

import android.app.NotificationManager
import androidx.annotation.VisibleForTesting
import androidx.core.app.NotificationCompat
import java.util.Collections
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SystemNotifier @Inject constructor() {

    private val notificationIds: MutableSet<NotificationId> = Collections.synchronizedSet(HashSet<NotificationId>())

    fun generateNotificationId(): NotificationId {
        while (true) {
            NotificationId(Random.nextInt())
                .takeIf { !notificationIds.contains(it) }
                ?.let {
                    notificationIds.add(it)
                    return it
                }
        }
    }

    @VisibleForTesting
    fun getSystemNotifications(): Set<NotificationId> {
        return notificationIds
    }
}

data class NotificationChannelDescription(val resStringId: Int)

data class NotificationChannelId(val id: String)

data class NotificationChannelName(val resStringId: Int)

data class NotificationId(val value: Int)

data class NotificationImportance(val importance: Int) {
    init {
        require(importance >= NotificationManager.IMPORTANCE_MIN &&
                importance <= NotificationManager.IMPORTANCE_MAX) { "invalid importance" }
    }
}

data class NotificationPriority(val priority: Int) {
    init {
        require(priority >= NotificationCompat.PRIORITY_MIN &&
                priority <= NotificationCompat.PRIORITY_MAX) { "invalid priority" }
    }
}
