package com.linagora.android.linshare.data.api

import com.linagora.android.linshare.data.model.AuthenticationAuditLogEntryUserResponse
import com.linagora.android.linshare.data.model.authentication.PermanentTokenBodyRequest
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface LinshareApi {

    @POST
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    suspend fun createPermanentToken(
        @Url authenticateUrl: String,
        @Header("Authorization") basicToken: String,
        @Body permanentToken: PermanentTokenBodyRequest
    ): Response<Token>

    @GET("/audit?type=AUTHENTICATION&action=SUCCESS")
    suspend fun auditAuthenticationEntryUser(): List<AuthenticationAuditLogEntryUserResponse>

    @GET("/authentication/authorized")
    @Headers("Accept: application/json")
    suspend fun isAuthorized(): User

    @GET("/quota/{uuid}")
    suspend fun getQuota(@Path("uuid") quotaUuid: String): AccountQuota
}
