package com.linagora.android.linshare.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.linagora.android.linshare.R
import com.linagora.android.linshare.util.SystemNotifier.CHANNEL_ID
import com.linagora.android.linshare.util.SystemNotifier.NOTIFICATION_CHANNEL_DESCRIPTION
import com.linagora.android.linshare.util.SystemNotifier.NOTIFICATION_CHANNEL_NAME
import com.linagora.android.linshare.util.SystemNotifier.NOTIFICATION_ID

object SystemNotifier {

    const val NOTIFICATION_CHANNEL_NAME = "LinShare Uploading"

    const val NOTIFICATION_CHANNEL_DESCRIPTION = "LinShare notification channel uploading"

    const val CHANNEL_ID = "VERBOSE_NOTIFICATION"

    const val NOTIFICATION_ID = 1
}

fun pushSystemNotification(title: String, message: String, context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = NOTIFICATION_CHANNEL_NAME
        val description = NOTIFICATION_CHANNEL_DESCRIPTION
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?

        notificationManager?.createNotificationChannel(channel)
    }

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)

    NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
}
