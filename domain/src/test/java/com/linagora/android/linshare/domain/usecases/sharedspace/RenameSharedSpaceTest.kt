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

package com.linagora.android.linshare.domain.usecases.sharedspace

import arrow.core.Either
import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.quota.QuotaId
import com.linagora.android.linshare.domain.model.sharedspace.LinShareNodeType
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpace
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleId
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.VersioningParameter
import com.linagora.android.linshare.domain.model.sharedspace.toRenameRequest
import com.linagora.android.linshare.domain.repository.sharedspace.SharedSpaceRepository
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_2
import com.linagora.android.testshared.TestFixtures
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.util.Date
import java.util.UUID

class RenameSharedSpaceTest {
    @Mock
    lateinit var sharedSpaceRepository: SharedSpaceRepository

    lateinit var renameSharedSpace: RenameSharedSpace

    private val newSharedSpace = SharedSpace(
        sharedSpaceId = SHARED_SPACE_ID_2,
        name = "test2",
        creationDate = Date(1574837876965),
        modificationDate = Date(1574837876965),
        role = SharedSpaceRole(SharedSpaceRoleId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce410")), SharedSpaceRoleName.ADMIN),
        quotaId = QuotaId(UUID.fromString("7371802c-f452-47a8-a65c-b5f0b36ce410")),
        versioningParameters = VersioningParameter(true),
        nodeType = LinShareNodeType.WORK_GROUP
    )

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)
        renameSharedSpace = RenameSharedSpace(sharedSpaceRepository)
    }

    @Test
    fun renameSharedSpaceShouldReturnSuccessStateWhenGetSharedSpaceSuccess() = runBlockingTest {
        `when`(sharedSpaceRepository.getSharedSpace(SHARED_SPACE_ID_2))
            .thenAnswer { newSharedSpace }

        `when`(sharedSpaceRepository.renameSharedSpace(SHARED_SPACE_ID_2, newSharedSpace.toRenameRequest("test2")))
            .thenAnswer { newSharedSpace }

        val state = renameSharedSpace(SHARED_SPACE_ID_2, "test2")
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList())

        Truth.assertThat(state).hasSize(2)
        Truth.assertThat(state).containsExactly(TestFixtures.State.LOADING_STATE, Either.right(RenameSharedSpaceSuccess(newSharedSpace)))
    }

    @Test
    fun renameSharedSpaceShouldReturnFailStateWhenGetSharedSpaceFail() = runBlockingTest {
        val renameException = RuntimeException()

        `when`(sharedSpaceRepository.getSharedSpace(SHARED_SPACE_ID_2))
            .thenThrow(renameException)

        `when`(sharedSpaceRepository.renameSharedSpace(SHARED_SPACE_ID_2, newSharedSpace.toRenameRequest("test2")))
            .thenAnswer { renameException }

        val state = renameSharedSpace(SHARED_SPACE_ID_2, "test2")
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList())

        Truth.assertThat(state).hasSize(2)
        Truth.assertThat(state).containsExactly(TestFixtures.State.LOADING_STATE, Either.left(GetSharedSpaceFailedInRename(renameException)))
    }

    @Test
    fun renameSharedSpaceShouldReturnFailStateWhenGetSharedSpaceSuccess() = runBlockingTest {
        val renameException = RuntimeException()

        `when`(sharedSpaceRepository.getSharedSpace(SHARED_SPACE_ID_2))
            .thenAnswer { newSharedSpace }

        `when`(sharedSpaceRepository.renameSharedSpace(SHARED_SPACE_ID_2, newSharedSpace.toRenameRequest("test2")))
            .thenThrow(renameException)

        val state = renameSharedSpace(SHARED_SPACE_ID_2, "test2")
            .map { it(TestFixtures.State.INIT_STATE) }
            .toList(ArrayList())

        Truth.assertThat(state).hasSize(2)
        Truth.assertThat(state).containsExactly(TestFixtures.State.LOADING_STATE, Either.left(RenameSharedSpaceFailure(renameException)))
    }
}
