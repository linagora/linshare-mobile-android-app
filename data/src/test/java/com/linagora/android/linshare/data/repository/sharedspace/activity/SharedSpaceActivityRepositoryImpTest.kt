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

package com.linagora.android.linshare.data.repository.sharedspace.activity

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.data.datasource.sharedspace.activity.SharedSpaceActivityDataSource
import com.linagora.android.testshared.SharedSpaceActivityFixtures.ALL_ACTIVITIES
import com.linagora.android.testshared.SharedSpaceActivityFixtures.COPY_DOCUMENT_LOG
import com.linagora.android.testshared.SharedSpaceActivityFixtures.CREATE_DOCUMENT_REVISION_LOG
import com.linagora.android.testshared.SharedSpaceActivityFixtures.CREATE_FOLDER_LOG
import com.linagora.android.testshared.SharedSpaceActivityFixtures.CREATE_MEMBER_LOG
import com.linagora.android.testshared.SharedSpaceActivityFixtures.CREATE_WORKGROUP_LOG
import com.linagora.android.testshared.SharedSpaceDocumentFixtures.SHARED_SPACE_ID_1
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class SharedSpaceActivityRepositoryImpTest {
    @Mock
    lateinit var sharedSpaceActivityDataSource: SharedSpaceActivityDataSource

    private lateinit var sharedSpaceActivityRepositoryImp: SharedSpaceActivityRepositoryImp

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        sharedSpaceActivityRepositoryImp = SharedSpaceActivityRepositoryImp(sharedSpaceActivityDataSource)
    }

    @Test
    fun getAllActivitiesShouldReturnAllWorkGroupActivities() = runBlockingTest {
        `when`(sharedSpaceActivityDataSource.getAllActivities(SHARED_SPACE_ID_1))
            .thenAnswer { ALL_ACTIVITIES }

        val activities = sharedSpaceActivityRepositoryImp.getAllActivities(SHARED_SPACE_ID_1)

        assertThat(activities).containsExactly(
            CREATE_FOLDER_LOG,
            CREATE_MEMBER_LOG,
            COPY_DOCUMENT_LOG,
            CREATE_DOCUMENT_REVISION_LOG,
            CREATE_WORKGROUP_LOG
        )
    }

    @Test
    fun getAllActivitiesShouldThrowExceptionWhenGetActivitiesFailed() = runBlockingTest {
        val getActivitiesException = RuntimeException("getActivities failed")
        `when`(sharedSpaceActivityDataSource.getAllActivities(SHARED_SPACE_ID_1))
            .thenThrow(getActivitiesException)

        assertThrows<RuntimeException> {
            runBlockingTest { sharedSpaceActivityRepositoryImp.getAllActivities(SHARED_SPACE_ID_1) }
        }
    }
}
