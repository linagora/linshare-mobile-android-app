package com.linagora.android.linshare.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.linagora.android.linshare.R
import com.linagora.android.linshare.notification.BaseNotification.Companion.DISABLE_PROGRESS
import com.linagora.android.linshare.notification.BaseNotification.Companion.DISABLE_PROGRESS_INDETERMINATE
import com.linagora.android.linshare.notification.BaseNotification.Companion.WAITING_PROGRESS
import com.linagora.android.linshare.notification.BaseNotification.Companion.WAITING_PROGRESS_INDETERMINATE
import com.linagora.android.linshare.util.AndroidUtils

abstract class BaseNotification(private val context: Context) {

    companion object {
        const val DISABLE_PROGRESS = 0

        const val WAITING_PROGRESS = 0

        const val DISABLE_PROGRESS_INDETERMINATE = false

        const val WAITING_PROGRESS_INDETERMINATE = true

        const val ONGOING_NOTIFICATION = true

        const val FINISHED_NOTIFICATION = false
    }

    private val baseBuilder = createNotificationBuilder()

    protected abstract fun getNotificationChannelName(): NotificationChannelName

    protected abstract fun getNotificationChannelDescription(): NotificationChannelDescription

    protected abstract fun getImportance(): NotificationImportance

    protected abstract fun getChannelId(): NotificationChannelId

    protected abstract fun getNotificationPriority(): NotificationPriority

    private fun createNotificationBuilder(): Builder {
        if (AndroidUtils.supportAndroidO()) {
            with(context) {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)
                    ?.let {
                        val baseChannel = createBaseChannel()
                        it.createNotificationChannel(baseChannel)
                    }
            }
        }

        return Builder(context, getChannelId().id)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setPriority(getNotificationPriority().priority)
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createBaseChannel(): NotificationChannel {
        with(context) {
            val name = getString(getNotificationChannelName().resStringId)
            val description = getString(getNotificationChannelDescription().resStringId)
            val importance = getImportance().importance

            val channel = NotificationChannel(getChannelId().id, name, importance)
            channel.description = description
            return channel
        }
    }

    fun create(notificationBuilder: Builder.() -> Notification): Notification {
        return notificationBuilder.invoke(baseBuilder)
    }

    fun notify(notificationId: NotificationId, notificationBuilder: Builder.() -> Notification) {
        NotificationManagerCompat.from(context)
            .notify(
                notificationId.value,
                notificationBuilder.invoke(baseBuilder)
            )
    }
}

fun Builder.showWaitingProgress(): Builder {
    this.setProgress(WAITING_PROGRESS, WAITING_PROGRESS, WAITING_PROGRESS_INDETERMINATE)
    return this
}

fun Builder.disableProgressBar(): Builder {
    this.setProgress(DISABLE_PROGRESS, DISABLE_PROGRESS, DISABLE_PROGRESS_INDETERMINATE)
    return this
}
