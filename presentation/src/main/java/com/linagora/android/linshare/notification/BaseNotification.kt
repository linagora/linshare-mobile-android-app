package com.linagora.android.linshare.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat.Builder
import androidx.core.app.NotificationManagerCompat
import com.linagora.android.linshare.R

abstract class BaseNotification(private val context: Context) {

    private val baseBuilder = createNotificationBuilder()

    protected abstract fun getNotificationChannelName(): NotificationChannelName

    protected abstract fun getNotificationChannelDescription(): NotificationChannelDescription

    protected abstract fun getImportance(): NotificationImportance

    protected abstract fun getChannelId(): NotificationChannelId

    protected abstract fun getNotificationPriority(): NotificationPriority

    private fun createNotificationBuilder(): Builder {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            with(context) {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)
                    ?.let {
                        val name = getString(getNotificationChannelName().resStringId)
                        val description = getString(getNotificationChannelDescription().resStringId)
                        val importance = getImportance().importance
                        val channel = NotificationChannel(getChannelId().id, name, importance)
                        channel.description = description
                        it.createNotificationChannel(channel)
                    }
            }
        }

        return Builder(context, getChannelId().id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(getNotificationPriority().priority)
    }

    fun notify(notificationId: NotificationId, notificationBuilder: Builder.() -> Notification) {
        NotificationManagerCompat.from(context)
            .notify(
                notificationId.value,
                notificationBuilder.invoke(baseBuilder)
            )
    }
}
