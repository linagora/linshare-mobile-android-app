/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

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
import com.linagora.android.linshare.notification.BaseNotification.Companion.ONGOING_NOTIFICATION
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
        .setOngoing(ONGOING_NOTIFICATION)
    return this
}

fun Builder.disableProgressBar(): Builder {
    this.setProgress(DISABLE_PROGRESS, DISABLE_PROGRESS, DISABLE_PROGRESS_INDETERMINATE)
    return this
}
