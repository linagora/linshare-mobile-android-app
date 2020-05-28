package com.linagora.android.linshare.data.api

import com.linagora.android.linshare.data.model.AuthenticationAuditLogEntryUserResponse
import com.linagora.android.linshare.data.model.authentication.PermanentTokenBodyRequest
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
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

    @DELETE("/jwt/{uuid}")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    suspend fun deletePermanentToken(
        @Path("uuid") tokenUuid: String
    ): Token

    @GET("/audit?type=AUTHENTICATION&action=SUCCESS")
    suspend fun auditAuthenticationEntryUser(): List<AuthenticationAuditLogEntryUserResponse>

    @GET("/authentication/authorized")
    @Headers("Accept: application/json")
    suspend fun getAuthorizedUser(): User

    @GET("/quota/{uuid}")
    @Headers("Accept: application/json")
    suspend fun getQuota(@Path("uuid") quotaUuid: String): AccountQuota

    @Multipart
    @POST("/documents")
    @Headers("Accept: application/json")
    suspend fun upload(
        @Part file: MultipartBody.Part,
        @Part("filesize") fileSize: Long
    ): Document

    @GET("/documents")
    @Headers("Accept: application/json")
    suspend fun getAll(): List<Document>

    @DELETE("/documents/{uuid}")
    @Headers("Accept: application/json")
    suspend fun removeDocument(@Path("uuid") uuid: String): Document

    @POST("/shares")
    @Headers("Accept: application/json")
    suspend fun share(@Body shareRequest: ShareRequest): List<Share>

    @GET("/received_shares")
    @Headers("Accept: application/json")
    suspend fun getReceivedShares(): List<Share>

    @GET("/autocomplete/{pattern}?type=SHARING")
    @Headers("Accept: application/json")
    suspend fun getSharingAutoComplete(
        @Path("pattern") pattern: String
    ): List<AutoCompleteResult>

    @POST("/documents/copy")
    @Headers("Accept: application/json")
    suspend fun copyInMySpace(@Body copyRequest: CopyRequest): List<Document>

    @GET("/shared_spaces?withRole=true")
    @Headers("Accept: application/json")
    suspend fun getSharedSpaces(): List<SharedSpaceNodeNested>

    @GET("/shared_spaces/{uuid}/nodes")
    @Headers("Accept: application/json")
    suspend fun getAllSharedSpaceNode(
        @Path("uuid") uuid: String,
        @Query("parent") parentUuid: String? = null
    ): List<WorkGroupNode>

    @GET("shared_spaces/{sharedSpaceId}/nodes/{nodeId}")
    @Headers("Accept: application/json")
    suspend fun getSharedSpaceNode(
        @Path("sharedSpaceId") sharedSpaceUuid: String,
        @Path("nodeId") sharedSpaceNodeUuid: String,
        @Query("tree") tree: Boolean = true
    ): WorkGroupNode

    @GET("shared_spaces/{uuid}")
    @Headers("Accept: application/json")
    suspend fun getSharedSpace(
        @Path("uuid") sharedSpaceUuid: String,
        @Query("members") includeMembers: Boolean = false,
        @Query("withRole") withRole: Boolean = true
    ): SharedSpace

    @Multipart
    @POST("shared_spaces/{sharedSpaceId}/nodes")
    @Headers("Accept: application/json")
    suspend fun uploadToSharedSpace(
        @Path("sharedSpaceId") sharedSpaceUuid: String,
        @Query("parent") parentUuid: String? = null,
        @Part file: MultipartBody.Part,
        @Part("filesize") fileSize: Long
    ): WorkGroupNode

    @DELETE("/shared_spaces/{sharedSpaceUuid}/nodes/{sharedSpaceNodeUuid}")
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json"
    )
    suspend fun removeSharedSpaceNode(
        @Path("sharedSpaceUuid") sharedSpaceUuid: String,
        @Path("sharedSpaceNodeUuid") sharedSpaceNodeUuid: String
    ): WorkGroupNode
}
