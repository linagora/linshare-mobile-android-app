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

package com.linagora.android.linshare.domain.usecases.upload

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.upload.TotalBytes
import com.linagora.android.linshare.domain.model.upload.TransferredBytes
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.linshare.domain.usecases.InteractorHandler
import com.linagora.android.linshare.domain.usecases.auth.AuthenticationViewState
import com.linagora.android.linshare.domain.usecases.auth.GetAuthenticatedInfoInteractor
import com.linagora.android.linshare.domain.usecases.quota.EnoughAccountQuotaInteractor
import com.linagora.android.linshare.domain.usecases.quota.ValidAccountQuota
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.NoOpOnFailure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.ViewStateStore
import com.linagora.android.linshare.domain.utils.sendState
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class UploadInteractor @Inject constructor(
    private val getAuthenticatedInfo: GetAuthenticatedInfoInteractor,
    private val enoughAccountQuotaInteractor: EnoughAccountQuotaInteractor,
    private val documentRepository: DocumentRepository,
    private val interactorHandler: InteractorHandler,
    private val viewStateStore: ViewStateStore
) {

    operator fun invoke(documentRequest: DocumentRequest): Flow<State<Either<Failure, Success>>> {
        return getAuthenticatedInfo()
            .map { viewStateStore.storeAndGet(it) }
            .flatMapConcat { authenticateState -> consumeAuthenticationState(documentRequest, authenticateState) }
            .flatMapConcat { quotaState -> reactToQuotaState(quotaState, documentRequest) }
    }

    private fun consumeAuthenticationState(
        documentRequest: DocumentRequest,
        authenticationState: Either<Failure, Success>
    ) = flow<Either<Failure, Success>> {
        emit(authenticationState)
        authenticationState.fold(NoOpOnFailure) { success ->
            if (success is AuthenticationViewState) {
                checkAccountQuota(this, documentRequest)
            }
        }
    }

    private suspend fun checkAccountQuota(
        flowCollector: FlowCollector<Either<Failure, Success>>,
        documentRequest: DocumentRequest
    ) {
        enoughAccountQuotaInteractor.invoke(documentRequest.file.length())
            .onStart { delay(500) }
            .map { viewStateStore.storeAndGet(it) }
            .filterNot { invalidState(it) }
            .collect { quotaState -> flowCollector.emit(quotaState) }
    }

    private fun invalidState(eitherState: Either<Failure, Success>): Boolean {
        return eitherState.exists { success -> success is Success.Loading }
    }

    private suspend fun reactToQuotaState(
        quotaState: Either<Failure, Success>,
        documentRequest: DocumentRequest
    ): Flow<State<Either<Failure, Success>>> {
        return channelFlow<State<Either<Failure, Success>>> {
            sendState { quotaState }
            quotaState.fold(NoOpOnFailure) { success ->
                if (success is ValidAccountQuota) {
                    uploadToMySpace(this, documentRequest)
                }
            }
        }
    }

    private suspend fun uploadToMySpace(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest
    ) {
        interactorHandler.handle(
            execution = { performUpload(producerScope, documentRequest) },
            onCatch = { UploadErrorHandler(producerScope)(it) }
        )
    }

    private suspend fun performUpload(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        documentRequest: DocumentRequest
    ) {
        val document = documentRepository.upload(
            documentRequest = documentRequest,
            onTransfer = { transferredBytes, totalBytes ->
                sendUploadingState(producerScope, transferredBytes, totalBytes)
            }
        )
        producerScope.sendState { Either.right(UploadSuccessViewState(document)) }
    }

    private fun sendUploadingState(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        transferredBytes: TransferredBytes,
        totalBytes: TotalBytes
    ) {
        producerScope.sendState { Either.right(UploadingViewState(transferredBytes, totalBytes)) }
    }
}
