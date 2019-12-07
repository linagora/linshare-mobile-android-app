package com.linagora.android.linshare.notification

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.linagora.android.linshare.R
import javax.inject.Inject

class UploadNotification @Inject constructor(
    private val context: Context
) : BaseNotification(context) {

    companion object {
        val CHANNEL_ID = NotificationChannelId("upload_channel_id")
    }

    override fun getNotificationChannelName(): NotificationChannelName {
        return NotificationChannelName(R.string.upload_channel_name)
    }

    override fun getNotificationChannelDescription(): NotificationChannelDescription {
        return NotificationChannelDescription(R.string.upload_channel_description)
    }

    override fun getImportance(): NotificationImportance {
        return NotificationImportance(NotificationManager.IMPORTANCE_HIGH)
    }

    override fun getChannelId(): NotificationChannelId = CHANNEL_ID

    override fun getNotificationPriority(): NotificationPriority {
        return NotificationPriority(NotificationCompat.PRIORITY_HIGH)
    }
}
