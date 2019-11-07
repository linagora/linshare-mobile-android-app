package com.linagora.android.linshare.data.api

import com.linagora.android.linshare.data.model.AuthenticationAuditLogEntryUserResponse
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.User
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Url

interface LinshareApi {

    @GET
    @Headers("Accept: application/json")
    suspend fun getPermanentToken(
        @Url authenticateUrl: String,
        @Header("Authorization") basicToken: String
    ): Response<Token>

    @GET("/audit?type=AUTHENTICATION&action=SUCCESS")
    suspend fun auditAuthenticationEntryUser(): List<AuthenticationAuditLogEntryUserResponse>

    @GET("/authentication/authorized")
    @Headers("Accept: application/json")
    suspend fun isAuthorized(): User

    @GET("/quota/{uuid}")
    suspend fun getQuota(@Path("uuid") quotaUuid: String): AccountQuota
}
