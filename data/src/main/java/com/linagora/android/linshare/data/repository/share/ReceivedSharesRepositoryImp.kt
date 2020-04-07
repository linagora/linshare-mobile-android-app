package com.linagora.android.linshare.data.repository.share

import com.linagora.android.linshare.data.datasource.ReceivedShareDataSource
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.repository.share.ReceivedShareRepository
import javax.inject.Inject

class ReceivedSharesRepositoryImp @Inject constructor(
    private val receivedShareDataSource: ReceivedShareDataSource
) : ReceivedShareRepository {
    override suspend fun getReceivedShares(): List<Share> {
        return receivedShareDataSource.getReceivedShares()
    }
}
