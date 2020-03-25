package com.linagora.android.linshare.data.repository.user

import com.linagora.android.linshare.data.datasource.LinshareDataSource
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.repository.user.UserRepository
import javax.inject.Inject

class LinshareUserRepository @Inject constructor(
    private val linshareDataSource: LinshareDataSource
) : UserRepository {

    override suspend fun getAuthorizedUser(): User? {
        return linshareDataSource.getAuthorizedUser()
    }
}
