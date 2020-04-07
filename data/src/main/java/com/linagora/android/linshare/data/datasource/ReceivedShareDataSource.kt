package com.linagora.android.linshare.data.datasource

import com.linagora.android.linshare.domain.model.share.Share

interface ReceivedShareDataSource {

    suspend fun getReceivedShares(): List<Share>
}
