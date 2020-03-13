package com.linagora.android.linshare.operator.download

import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.document.Document

interface DownloadOperator {

    suspend fun downloadDocument(credential: Credential, token: Token, document: Document)
}
