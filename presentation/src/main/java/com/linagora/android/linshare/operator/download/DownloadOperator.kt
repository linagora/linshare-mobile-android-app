package com.linagora.android.linshare.operator.download

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token

interface DownloadOperator {

    suspend fun download(credential: Credential, token: Token, downloadRequest: DownloadRequest)
}
