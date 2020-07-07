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

package com.linagora.android.linshare.data.repository.sharedspace

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.SharedSpaceDataSource
import com.linagora.android.linshare.domain.model.sharedspace.MembersParameter
import com.linagora.android.linshare.domain.model.sharedspace.RolesParameter
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.usecases.sharedspace.CreateSharedSpaceException
import com.linagora.android.testshared.SharedSpaceDocumentFixtures
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import com.linagora.android.testshared.SharedSpaceFixtures.CREATE_WORK_GROUP_REQUEST
import com.linagora.android.testshared.SharedSpaceFixtures.QUERY_STRING_SHARED_SPACE
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_1
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SharedSpaceRepositoryImpTest {

    @Mock
    lateinit var sharedSpaceDataSource: SharedSpaceDataSource

    private lateinit var sharedSpaceRepositoryImp: SharedSpaceRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharedSpaceRepositoryImp = SharedSpaceRepositoryImp(sharedSpaceDataSource)
    }

    @Test
    fun getAllSharedSpaceShouldReturnSharedSpaceList() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.getSharedSpaces())
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_2) }

            val sharedSpace = sharedSpaceRepositoryImp.getSharedSpaces()
            assertThat(sharedSpace).containsExactly(SHARED_SPACE_1, SHARED_SPACE_2)
        }
    }

    @Test
    fun getAllSharedSpaceShouldReturnEmptyListWhenNoSharedSpaceExist() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.getSharedSpaces())
                .thenAnswer { emptyList<SharedSpaceNodeNested>() }

            val sharedSpace = sharedSpaceRepositoryImp.getSharedSpaces()
            assertThat(sharedSpace).isEmpty()
        }
    }

    @Test
    fun getSharedSpaceShouldReturnAnExistedSharedSpace() = runBlockingTest {
        `when`(
            sharedSpaceDataSource.getSharedSpace(
                SHARED_SPACE_ID_1,
                MembersParameter.WithoutMembers,
                RolesParameter.WithRole
            )
        )
            .thenAnswer { SharedSpaceDocumentFixtures.SHARED_SPACE_1 }

        val sharedSpace = sharedSpaceRepositoryImp
            .getSharedSpace(
                SHARED_SPACE_ID_1,
                MembersParameter.WithoutMembers,
                RolesParameter.WithRole
            )

        assertThat(sharedSpace).isEqualTo(SharedSpaceDocumentFixtures.SHARED_SPACE_1)
    }

    @Test
    fun searchShouldReturnResultList() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.searchSharedSpaces(QUERY_STRING_SHARED_SPACE))
                .thenAnswer { listOf(SHARED_SPACE_1, SHARED_SPACE_2) }

            val documents = sharedSpaceRepositoryImp.search(QUERY_STRING_SHARED_SPACE)
            assertThat(documents).containsExactly(SHARED_SPACE_1, SHARED_SPACE_2)
        }
    }

    @Test
    fun searchShouldReturnResultEmptyList() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.searchSharedSpaces(QUERY_STRING_SHARED_SPACE))
                .thenAnswer { emptyList<SharedSpaceNodeNested>() }

            val documents = sharedSpaceRepositoryImp.search(QUERY_STRING_SHARED_SPACE)
            assertThat(documents).isEmpty()
        }
    }

    @Test
    fun createSharedSpaceShouldReturnNewWorkGroup() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.createWorkGroup(CREATE_WORK_GROUP_REQUEST))
                .thenAnswer { SharedSpaceDocumentFixtures.SHARED_SPACE_1 }

            val sharedSpace = sharedSpaceRepositoryImp.createWorkGroup(CREATE_WORK_GROUP_REQUEST)
            assertThat(sharedSpace).isEqualTo(SharedSpaceDocumentFixtures.SHARED_SPACE_1)
        }
    }

    @Test
    fun createSharedSpaceShouldThrowWhenDataSourceFailedToCreateWorkGroup() {
        runBlockingTest {
            `when`(sharedSpaceDataSource.createWorkGroup(CREATE_WORK_GROUP_REQUEST))
                .thenThrow(CreateSharedSpaceException(RuntimeException()))

            assertThrows<CreateSharedSpaceException> {
                runBlockingTest {
                    sharedSpaceRepositoryImp.createWorkGroup(CREATE_WORK_GROUP_REQUEST)
                }
            }
        }
    }
}
