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

package com.linagora.android.linshare.utils

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.SuggestNewNameUtils
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class SuggestNewNameUtilsTest {

    private lateinit var context: Context

    private lateinit var suggestNewNameUtils: SuggestNewNameUtils

    companion object {

        private const val NEW_WORKGROUP = "New workgroup"
        private const val NEW_WORKGROUP_1 = "New workgroup (1)"
        private const val NEW_WORKGROUP_2 = "New workgroup (2)"
        private const val NEW_WORKGROUP_3 = "New workgroup (3)"

        private val FULL_NEW_WORKGROUP_LIST = listOf(
            NEW_WORKGROUP_2,
            NEW_WORKGROUP_1,
            NEW_WORKGROUP
        )

        private val FULL_NEW_WORKGROUP_LIST_MISS_ITEM_IN_MIDLE_LIST = listOf(
            NEW_WORKGROUP_2,
            NEW_WORKGROUP
        )

        private val FULL_NEW_WORKGROUP_LIST_MISS_FIRST_ITEM = listOf(
            NEW_WORKGROUP_2,
            NEW_WORKGROUP_1
        )

        private val LIST_NOT_HAVE_NEW_WORKGROUP = listOf("testName1", "testName2")

        private val LIST_EMPTY = listOf<String>()

        private val LIST_RANDOM = listOf("testName2",
            NEW_WORKGROUP_2, "testName1",
            NEW_WORKGROUP
        )
    }

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        suggestNewNameUtils = SuggestNewNameUtils(context)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNextItemWithFullNewWorkGroupList() {
        assertThat(suggestNewNameUtils.suggestNewName(FULL_NEW_WORKGROUP_LIST, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP_3)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupIndexWithFullNewWorkGroupListMissingItemAtMidle() {
        assertThat(suggestNewNameUtils.suggestNewName(FULL_NEW_WORKGROUP_LIST_MISS_ITEM_IN_MIDLE_LIST, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP_1)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithFullNewWorkGroupLisMissingItemAtFirst() {
        assertThat(suggestNewNameUtils.suggestNewName(FULL_NEW_WORKGROUP_LIST_MISS_FIRST_ITEM, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithListNotContainNewWorkGroupName() {
        assertThat(suggestNewNameUtils.suggestNewName(LIST_NOT_HAVE_NEW_WORKGROUP, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithListEmpty() {
        assertThat(suggestNewNameUtils.suggestNewName(LIST_EMPTY, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP)
    }

    @Test
    fun suggestNameWorkgroupShouldReturnNewWorkGroupWithListRandom() {
        assertThat(suggestNewNameUtils.suggestNewName(LIST_RANDOM, SuggestNewNameUtils.SuggestNameType.WORKGROUP))
            .isEqualTo(NEW_WORKGROUP_1)
    }
}
