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

package com.linagora.android.linshare.view.upload.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.linagora.android.linshare.R
import com.linagora.android.linshare.inject.worker.ChildWorkerFactory
import com.linagora.android.linshare.notification.BaseNotification
import com.linagora.android.linshare.notification.SystemNotifier
import com.linagora.android.linshare.notification.UploadAndDownloadNotification
import com.linagora.android.linshare.notification.disableProgressBar
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.RESULT_MESSAGE
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_RESULT
import org.slf4j.LoggerFactory
import javax.inject.Inject

class UploadCompletedNotificationWorker @Inject constructor(
    private val context: Context,
    private val params: WorkerParameters,
    private val systemNotifier: SystemNotifier,
    private val uploadNotification: UploadAndDownloadNotification
) : CoroutineWorker(context, params) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(UploadCompletedNotificationWorker::class.java)
    }

    override suspend fun doWork(): Result {
        try {
            val uploadResult = inputData.getString(UPLOAD_RESULT)
            val message = inputData.getString(RESULT_MESSAGE)
            require(!uploadResult.isNullOrEmpty())
            require(!message.isNullOrEmpty())

            notifyUploadCompleted(
                uploadResult = UploadResult.valueOf(inputData.getString(UPLOAD_RESULT)!!),
                message = message
            )
        } catch (throwable: Throwable) {
            LOGGER.error("doWork(): $throwable")
            notifyUploadCompleted(UploadResult.UPLOAD_SUCCESS, getFallbackUploadMessage())
        }
        return Result.success()
    }

    private fun getFallbackUploadMessage(): String {
        return context.getString(R.string.upload_success)
    }

    private fun notifyUploadCompleted(uploadResult: UploadResult, message: String) {
        uploadNotification.notify(systemNotifier.generateNotificationId()) {
            this.setContentTitle(getNotificationTitle(uploadResult))
                .setOngoing(BaseNotification.FINISHED_NOTIFICATION)
                .setContentText(message)
                .disableProgressBar()
            this.build()
        }
    }

    private fun getNotificationTitle(uploadResult: UploadResult): String {
        return when (uploadResult) {
            UploadResult.UPLOAD_FAILED -> context.getString(R.string.upload_failed)
            else -> context.getString(R.string.upload_success)
        }
    }

    class Factory @Inject constructor(
        private val systemNotifier: SystemNotifier,
        private val uploadAndDownloadNotification: UploadAndDownloadNotification
    ) : ChildWorkerFactory {
        override fun create(
            applicationContext: Context,
            params: WorkerParameters
        ): ListenableWorker {
            return UploadCompletedNotificationWorker(
                applicationContext,
                params,
                systemNotifier,
                uploadAndDownloadNotification
            )
        }
    }
}
