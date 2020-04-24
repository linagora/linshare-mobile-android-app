package com.linagora.android.linshare.view.upload.controller

import arrow.core.Either
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import kotlinx.coroutines.flow.Flow

interface UploadCommand {

    val documentRequest: DocumentRequest

    suspend fun execute(): Flow<State<Either<Failure, Success>>>
}
