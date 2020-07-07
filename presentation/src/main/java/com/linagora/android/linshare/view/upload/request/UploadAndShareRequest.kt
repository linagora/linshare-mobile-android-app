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

package com.linagora.android.linshare.view.upload.request

import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.linagora.android.linshare.domain.model.GenericUser
import com.linagora.android.linshare.domain.model.autocomplete.MailingList
import com.linagora.android.linshare.util.append
import com.linagora.android.linshare.view.share.worker.ShareWorker
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.MAILING_LISTS_KEY
import com.linagora.android.linshare.view.share.worker.ShareWorker.Companion.RECIPIENTS_KEY
import com.linagora.android.linshare.view.upload.worker.UploadCompletedNotificationWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker
import com.linagora.android.linshare.view.upload.worker.UploadWorker.Companion.UPLOAD_REQUEST_TYPE

class UploadAndShareRequest constructor(
    private val workManager: WorkManager,
    private val recipients: Set<GenericUser>,
    private val mailingLists: Set<MailingList>
) : UploadWorkerRequest {

    override fun execute(inputData: Data) {
        val data = inputData.append(workDataOf(
            UPLOAD_REQUEST_TYPE to UploadRequestType.UploadAndShare.name,
            RECIPIENTS_KEY to recipients.map { it.mail }.toTypedArray(),
            MAILING_LISTS_KEY to mailingLists.map { mailingList -> mailingList.mailingListId }
                .map { it.uuid.toString() }
                .toTypedArray()
        ))

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(data)
            .setConstraints(constraints)
            .addTag(UploadWorker.TAG_UPLOAD_WORKER)
            .build()

        val shareWorker = OneTimeWorkRequestBuilder<ShareWorker>()
            .setConstraints(constraints)
            .addTag(ShareWorker.TAG_SHARE_WORKER)
            .build()

        val uploadCompletedNotification = OneTimeWorkRequestBuilder<UploadCompletedNotificationWorker>()
            .build()

        workManager.beginWith(uploadRequest)
            .then(shareWorker)
            .then(uploadCompletedNotification)
            .enqueue()
    }
}
