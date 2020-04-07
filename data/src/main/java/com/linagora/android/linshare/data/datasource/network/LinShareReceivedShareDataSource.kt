package com.linagora.android.linshare.data.datasource.network

import com.linagora.android.linshare.data.api.LinshareApi
import com.linagora.android.linshare.data.datasource.ReceivedShareDataSource
import com.linagora.android.linshare.domain.model.share.Share
import javax.inject.Inject

class LinShareReceivedShareDataSource @Inject constructor(
    private val linshareApi: LinshareApi
) : ReceivedShareDataSource {

    override suspend fun getReceivedShares(): List<Share> {
        return linshareApi.getReceivedShares().sortedByDescending { it.creationDate }
    }
}
