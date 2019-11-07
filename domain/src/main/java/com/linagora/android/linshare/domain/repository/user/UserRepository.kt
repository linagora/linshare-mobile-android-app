package com.linagora.android.linshare.domain.repository.user

import com.linagora.android.linshare.domain.model.User

interface UserRepository {

    suspend fun getAuthorizedUser(): User?
}
