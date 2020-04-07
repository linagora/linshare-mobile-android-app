package com.linagora.android.linshare.domain.repository.share

import com.linagora.android.linshare.domain.model.share.Share

interface ReceivedShareRepository {

    suspend fun getReceivedShares(): List<Share>
}
