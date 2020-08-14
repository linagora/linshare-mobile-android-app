/*
 * LinShare is an open source filesharing software, part of the LinPKI software
 * suite, developed by Linagora.
 *
 * Copyright (C) 2020 LINAGORA
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Affero General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version,
 * provided you comply with the Additional Terms applicable for LinShare software by
 * Linagora pursuant to Section 7 of the GNU Affero General Public License,
 * subsections (b), (c), and (e), pursuant to which you must notably (i) retain the
 * display in the interface of the “LinShare™” trademark/logo, the "Libre & Free" mention,
 * the words “You are using the Free and Open Source version of LinShare™, powered by
 * Linagora © 2009–2020. Contribute to Linshare R&D by subscribing to an Enterprise
 * offer!”. You must also retain the latter notice in all asynchronous messages such as
 * e-mails sent with the Program, (ii) retain all hypertext links between LinShare and
 * http://www.linshare.org, between linagora.com and Linagora, and (iii) refrain from
 * infringing Linagora intellectual property rights over its trademarks and commercial
 * brands. Other Additional Terms apply, see
 * <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf>
 * for more details.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 * You should have received a copy of the GNU Affero General Public License and its
 * applicable Additional Terms for LinShare along with this program. If not, see
 * <http://www.gnu.org/licenses/> for the GNU Affero General Public License version
 *  3 and <http://www.linshare.org/licenses/LinShare-License_AfferoGPL-v3.pdf> for
 *  the Additional Terms applicable to LinShare software.
 */

package com.linagora.android.linshare.data.api

import com.linagora.android.linshare.data.model.AuthenticationAuditLogEntryUserResponse
import com.linagora.android.linshare.data.model.authentication.PermanentTokenBodyRequest
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.Token
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.model.audit.AuditLogEntryUser
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteResult
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.model.sharedspace.CreateWorkGroupRequest
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.linshare.domain.model.sharedspace.member.AddMemberRequest
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
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

    @POST("/shared_spaces/{destinationSharedSpaceId}/nodes/{destinationParentNodeId}/copy")
    @Headers("Accept: application/json")
    suspend fun copyWorkGroupNodeToSharedSpaceDestination(
        @Path("destinationSharedSpaceId") destinationSharedSpaceId: String,
        @Path("destinationParentNodeId") destinationParentNodeId: String? = null,
        @Query("deleteShare")
        @Body copyRequest: CopyRequest
    ): List<WorkGroupNode>

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

    @GET("/shared_spaces/{sharedSpaceUuid}/members")
    @Headers("Accept: application/json")
    suspend fun getMembers(
        @Path("sharedSpaceUuid") sharedSpaceUuid: String
    ): List<SharedSpaceMember>

    @GET("/shared_space_roles")
    @Headers("Accept: application/json")
    suspend fun getSharedSpaceRoles(): List<SharedSpaceRole>

    @GET("/autocomplete/{pattern}?type=THREAD_MEMBERS")
    @Headers("Accept: application/json")
    suspend fun getAutoCompleteThreadMembers(
        @Path("pattern") pattern: String,
        @Query("threadUuid") threadUuid: String
    ): List<AutoCompleteResult>

    @POST("/shared_spaces/{sharedSpaceId}/members")
    @Headers("Accept: application/json")
    suspend fun addMember(
        @Path("sharedSpaceId") sharedSpaceId: String,
        @Body addMemberRequest: AddMemberRequest
    ): SharedSpaceMember

    @POST("/shared_spaces")
    @Headers("Accept: application/json")
    suspend fun createWorkGroup(@Body createWorkGroupRequest: CreateWorkGroupRequest): SharedSpace

    @GET("/work_groups/{workGroupId}/audit")
    @Headers("Accept: application/json")
    suspend fun findAllWorkGroupActivities(
        @Path("workGroupId") workGroupId: String
    ): List<AuditLogEntryUser>

    @PUT("/shared_spaces/{sharedSpaceId}/members/{memberUuid}")
    @Headers("Accept: application/json")
    suspend fun editRoleMember(
        @Path("sharedSpaceId") sharedSpaceId: String,
        @Path("memberUuid") memberUuid: String,
        @Body editMemberRequest: AddMemberRequest
    ): SharedSpaceMember
}
