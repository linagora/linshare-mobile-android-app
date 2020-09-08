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

package com.linagora.android.linshare.domain.utils

import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode
import com.linagora.android.testshared.SharedSpaceDocumentFixtures
import com.linagora.android.testshared.SharedSpaceFixtures
import org.junit.Test

class ListExtensionsTest {

    @Test
    fun sortWorkGroupNodeShouldReturnSortedAscendingWithTypeAscendingName() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.AscendingName))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnSortedDescendingWithTypeDescendingName() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.DescendingName))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnSortedAscendingWithTypeAscendingModificationDate() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.AscendingModificationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnSortedDescendingWithTypeDescendingModificationDate() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.DescendingModificationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnSortedAscendingWithTypeAscendingCreationDate() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.AscendingCreationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnSortedDescendingWithTypeDescendingCreationDate() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.DescendingCreationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnSortedAscendingWithTypeAscendingFileSize() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.AscendingFileSize))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnSortedDescendingWithTypeDescendingFileSize() {
        val inputList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2
        )

        val expectedList = listOf(
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_1,
            SharedSpaceDocumentFixtures.WORK_GROUP_FOLDER_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_2,
            SharedSpaceDocumentFixtures.WORK_GROUP_DOCUMENT_3
        )

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.DescendingFileSize))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortWorkGroupNodeShouldReturnEmptyWithEmptyList() {
        val inputList = ArrayList<WorkGroupNode>()

        val expectedList = ArrayList<WorkGroupNode>()

        Truth.assertThat(inputList.sortBy(OrderListConfigurationType.AscendingName))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortSharedSpaceNodeNestedShouldReturnSortedAscendingWithTypeAscendingName() {
        val inputList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5, SharedSpaceFixtures.SHARED_SPACE_2
        )

        val expectedList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1,
            SharedSpaceFixtures.SHARED_SPACE_2,
            SharedSpaceFixtures.SHARED_SPACE_5
        )

        Truth.assertThat(inputList.sortSharedSpaceNodeNestedBy(OrderListConfigurationType.AscendingName))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortSharedSpaceNodeNestedShouldReturnSortedDescendingWithTypeDescendingName() {
        val inputList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5, SharedSpaceFixtures.SHARED_SPACE_2
        )

        val expectedList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_5,
            SharedSpaceFixtures.SHARED_SPACE_2,
            SharedSpaceFixtures.SHARED_SPACE_1
        )

        Truth.assertThat(inputList.sortSharedSpaceNodeNestedBy(OrderListConfigurationType.DescendingName))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortSharedSpaceNodeNestedShouldReturnSortedAscendingWithTypeAscendingModificationDate() {
        val inputList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5, SharedSpaceFixtures.SHARED_SPACE_6
        )

        val expectedList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1,
            SharedSpaceFixtures.SHARED_SPACE_5,
            SharedSpaceFixtures.SHARED_SPACE_6
        )

        Truth.assertThat(inputList.sortSharedSpaceNodeNestedBy(OrderListConfigurationType.AscendingModificationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortSharedSpaceNodeNestedShouldReturnSortedDescendingWithTypeDescendingModificationDate() {
        val inputList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5, SharedSpaceFixtures.SHARED_SPACE_6
        )

        val expectedList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_6,
            SharedSpaceFixtures.SHARED_SPACE_1,
            SharedSpaceFixtures.SHARED_SPACE_5
        )

        Truth.assertThat(inputList.sortSharedSpaceNodeNestedBy(OrderListConfigurationType.DescendingModificationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortSharedSpaceNodeNestedShouldReturnSortedAscendingWithTypeAscendingCreationDate() {
        val inputList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5, SharedSpaceFixtures.SHARED_SPACE_6
        )

        val expectedList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1,
            SharedSpaceFixtures.SHARED_SPACE_5,
            SharedSpaceFixtures.SHARED_SPACE_6
        )

        Truth.assertThat(inputList.sortSharedSpaceNodeNestedBy(OrderListConfigurationType.AscendingCreationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortSharedSpaceNodeNestedShouldReturnSortedAscendingWithTypeDescendingCreationDate() {
        val inputList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5, SharedSpaceFixtures.SHARED_SPACE_6
        )

        val expectedList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_6,
            SharedSpaceFixtures.SHARED_SPACE_1,
            SharedSpaceFixtures.SHARED_SPACE_5
        )

        Truth.assertThat(inputList.sortSharedSpaceNodeNestedBy(OrderListConfigurationType.DescendingCreationDate))
            .isEqualTo(expectedList)
    }

    @Test
    fun sortSharedSpaceNodeNestedShouldReturnSortedAscendingModificationDateWithOtherType() {
        val inputList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1, SharedSpaceFixtures.SHARED_SPACE_5, SharedSpaceFixtures.SHARED_SPACE_6
        )

        val expectedList = listOf(
            SharedSpaceFixtures.SHARED_SPACE_1,
            SharedSpaceFixtures.SHARED_SPACE_5,
            SharedSpaceFixtures.SHARED_SPACE_6
        )

        Truth.assertThat(inputList.sortSharedSpaceNodeNestedBy(OrderListConfigurationType.AscendingFileSize))
            .isEqualTo(expectedList)
    }
}
