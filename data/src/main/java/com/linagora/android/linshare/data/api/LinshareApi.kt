package com.linagora.android.linshare.data.api

import com.linagora.android.linshare.data.model.AuthenticationAuditLogEntryUserResponse
import com.linagora.android.linshare.domain.model.Token
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
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
}
