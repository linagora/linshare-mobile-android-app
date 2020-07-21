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

package com.linagora.android.linshare.receiver

import android.content.Context
import android.content.Intent
import androidx.lifecycle.asFlow
import androidx.work.Operation
import androidx.work.WorkManager
import com.linagora.android.linshare.R
import com.linagora.android.linshare.notification.NotificationId
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.util.CoroutinesDispatcherProvider
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_WORKER_FILE_NAME
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_WORKER_NOTIFICATION
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_WORKER_UUID
import dagger.android.DaggerBroadcastReceiver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CancelUploadRequestReceiver : DaggerBroadcastReceiver() {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(CancelUploadRequestReceiver::class.java)

        private const val INVALID_NOTIFICATION_VALUE = -1

        private const val CANCEL_UPLOAD_REQUEST = "com.linagora.android.linshare.CANCEL_UPLOAD"

        val CANCEL_UPLOAD_INTENT = Intent(CANCEL_UPLOAD_REQUEST)
    }

    @Inject
    lateinit var appContext: Context

    @Inject
    lateinit var dispatcherProvider: CoroutinesDispatcherProvider

    @Inject
    lateinit var uploadNotification: UploadAndDownloadNotification

    @Inject
    lateinit var systemNotifier: SystemNotifier

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        LOGGER.info("onReceive(): $intent")
        try {
            val workerUUID = intent?.getStringExtra(UPLOAD_WORKER_UUID)
            val notificationId = intent?.getIntExtra(UPLOAD_WORKER_NOTIFICATION, INVALID_NOTIFICATION_VALUE)
            val uploadFileName = intent?.getStringExtra(UPLOAD_WORKER_FILE_NAME)

            checkNotNull(workerUUID)

            val arguments = Triple<UUID, NotificationId, String?>(
                UUID.fromString(workerUUID),
                notificationId.takeIf { it != INVALID_NOTIFICATION_VALUE }
                    ?.let(::NotificationId)
                    ?: systemNotifier.generateNotificationId(),
                uploadFileName
            )

            GlobalScope.launch {
                arguments.let { WorkManager.getInstance(appContext).cancelWorkById(it.first) }
                    .state.asFlow()
                    .collectLatest { reactToCancelState(arguments.second, it, arguments.third) }
            }
        } catch (throwable: Throwable) {
            LOGGER.error("onReceive(): ${throwable.message}")
            LOGGER.error("onReceive(): ${throwable.printStackTrace()}")
        }
    }

    private suspend fun reactToCancelState(
        notificationId: NotificationId,
        state: Operation.State,
        uploadFileName: String?
    ) {
        LOGGER.info("reactToCancelState(): $state")
        when (state) {
            is Operation.State.SUCCESS -> notifyCancelUpload(notificationId, uploadFileName)
            is Operation.State.FAILURE -> {
                state.throwable.printStackTrace()
                LOGGER.error("reactToCancelState(): ${state.throwable.message}")
            }
        }
    }

    private fun notifyCancelUpload(notificationId: NotificationId, fileName: String?) {
        LOGGER.info("notifyCancelUpload(): $notificationId - $fileName")
        uploadNotification.notify(notificationId) {
            this.setContentTitle(appContext.getString(R.string.upload_cancelled))
                .setContentText(fileName)
                .build()
        }
    }
}
