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
import com.linagora.android.linshare.data.datasource.sharedspace.member.SharedSpaceMemberDataSource
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMember
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.ADD_BAR_FOO_MEMBER_REQUEST
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.BAR_FOO_MEMBER
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.JOHN_DOE_MEMBER
import com.linagora.android.testshared.SharedSpaceFixtures.SHARED_SPACE_ID_1
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SharedSpaceMemberRepositoryImpTest {

    @Mock
    lateinit var linSharedSpaceMemberDataSource: SharedSpaceMemberDataSource

    private lateinit var sharedSpaceMemberRepositoryImp: SharedSpaceMemberRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharedSpaceMemberRepositoryImp = SharedSpaceMemberRepositoryImp(linSharedSpaceMemberDataSource)
    }

    @Test
    fun getAllMembersShouldReturnAllMemberInSharedSpace() = runBlockingTest {
        `when`(linSharedSpaceMemberDataSource.getAllMembers(SHARED_SPACE_ID_1))
            .thenAnswer { listOf(JOHN_DOE_MEMBER, BAR_FOO_MEMBER) }

        val members = sharedSpaceMemberRepositoryImp.getAllMembers(SHARED_SPACE_ID_1)

        assertThat(members).containsExactly(JOHN_DOE_MEMBER, BAR_FOO_MEMBER)
    }

    @Test
    fun getAllMembersShouldNoMemberInSharedSpace() = runBlockingTest {
        `when`(linSharedSpaceMemberDataSource.getAllMembers(SHARED_SPACE_ID_1))
            .thenAnswer { emptyList<SharedSpaceMember>() }

        val members = sharedSpaceMemberRepositoryImp.getAllMembers(SHARED_SPACE_ID_1)

        assertThat(members).isEmpty()
    }

    @Test
    fun getAllMembersShouldThrowWhenGetMembersFailed() = runBlockingTest {
        val exception = RuntimeException("get member failed")
        `when`(linSharedSpaceMemberDataSource.getAllMembers(SHARED_SPACE_ID_1))
            .thenThrow(exception)

        assertThrows<RuntimeException> { runBlockingTest {
            sharedSpaceMemberRepositoryImp.getAllMembers(SHARED_SPACE_ID_1) } }
    }

    @Test
    fun addMemberShouldAddMemberToSharedSpaceWhenAddMemberRequestValid() = runBlockingTest {
        `when`(linSharedSpaceMemberDataSource.addMember(ADD_BAR_FOO_MEMBER_REQUEST))
            .thenAnswer { BAR_FOO_MEMBER }

        val addedMember = sharedSpaceMemberRepositoryImp
            .addMember(ADD_BAR_FOO_MEMBER_REQUEST)

        assertThat(addedMember).isEqualTo(BAR_FOO_MEMBER)
    }
}
