package com.linagora.android.testshared

import arrow.core.Either
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.copy.SpaceType
import com.linagora.android.linshare.domain.usecases.myspace.CopyInMySpaceSuccess
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import java.util.UUID

object CopyFixtures {
    val COPY_REQUEST_1 = CopyRequest(UUID.fromString("77d10c28-583c-45a8-b747-d8a028f980bb"), SpaceType.RECEIVED_SHARE)

    val COPY_SUCCESS_STATE_1 = Either.Right(CopyInMySpaceSuccess(listOf(DOCUMENT)))
}
