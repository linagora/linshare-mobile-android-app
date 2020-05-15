package com.linagora.android.linshare.domain.model

import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.quota.QuotaSize

data class AccountQuota(
    val quota: QuotaSize,
    val usedSpace: QuotaSize,
    val maxFileSize: QuotaSize,
    val maintenance: Boolean
)

fun AccountQuota.enoughQuotaToUpload(documentRequest: DocumentRequest): Boolean {
    return documentRequest.file.length() < (quota - usedSpace)
}

fun AccountQuota.validMaxFileSizeToUpload(documentRequest: DocumentRequest): Boolean {
    return documentRequest.file.length() < maxFileSize.size
}
