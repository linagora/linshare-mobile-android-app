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
import com.linagora.android.testshared.ShareFixtures.SHARE_1
import com.linagora.android.testshared.ShareFixtures.SHARE_3
import com.linagora.android.testshared.ShareFixtures.SHARE_4
import com.linagora.android.testshared.SharedSpaceDocumentFixtures
import com.linagora.android.testshared.SharedSpaceFixtures
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_2
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_3
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_4
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ListExtensionsTest {

    @Nested
    inner class WorkGroupNodeTest {
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
    }

    @Nested
    inner class SharedSpaceNodeTest {
        @Test
        fun sortSharedSpaceNodeNestedShouldReturnSortedAscendingWithTypeAscendingName() {
            val inputList = listOf(
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_2
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
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_2
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
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_6
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
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_6
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
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_6
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
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_6
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
                SharedSpaceFixtures.SHARED_SPACE_1,
                SharedSpaceFixtures.SHARED_SPACE_5,
                SharedSpaceFixtures.SHARED_SPACE_6
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

    @Nested
    inner class DocumentTest {
        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeAscendingName() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT_3,
                DOCUMENT_4,
                DOCUMENT,
                DOCUMENT_2
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.AscendingName))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedDescendingWithTypeDescendingName() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT_2,
                DOCUMENT,
                DOCUMENT_4,
                DOCUMENT_3
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.DescendingName))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeAscendingCreationDate() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT_4,
                DOCUMENT_3,
                DOCUMENT,
                DOCUMENT_2
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.AscendingCreationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeDescendingCreationDate() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT,
                DOCUMENT_2,
                DOCUMENT_3,
                DOCUMENT_4
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.DescendingCreationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeAscendingModificationDate() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT_4,
                DOCUMENT_3,
                DOCUMENT,
                DOCUMENT_2
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.AscendingModificationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeDescendingModificationDate() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT,
                DOCUMENT_2,
                DOCUMENT_3,
                DOCUMENT_4
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.DescendingModificationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeAscendingFileSize() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT,
                DOCUMENT_2,
                DOCUMENT_3,
                DOCUMENT_4
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.AscendingFileSize))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeDescendingFileSize() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT_4,
                DOCUMENT_3,
                DOCUMENT,
                DOCUMENT_2
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.DescendingFileSize))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeAscendingShared() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT,
                DOCUMENT_2,
                DOCUMENT_3,
                DOCUMENT_4
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.AscendingShared))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortDocumentShouldReturnSortedAscendingWithTypeDescendingShared() {
            val inputList = listOf(
                DOCUMENT, DOCUMENT_2, DOCUMENT_3, DOCUMENT_4
            )

            val expectedList = listOf(
                DOCUMENT_4,
                DOCUMENT_3,
                DOCUMENT,
                DOCUMENT_2
            )

            Truth.assertThat(inputList.sortDocumentBy(OrderListConfigurationType.DescendingShared))
                .isEqualTo(expectedList)
        }
    }

    @Nested
    inner class ShareTest {
        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeAscendingName() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_1,
                SHARE_3,
                SHARE_4
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.AscendingName))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedDescendingWithTypeDescendingName() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_4,
                SHARE_3,
                SHARE_1
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.DescendingName))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeAscendingModificationDate() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_3,
                SHARE_4,
                SHARE_1
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.AscendingModificationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeDescendingModificationDate() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_1,
                SHARE_4,
                SHARE_3
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.DescendingModificationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeAscendingCreationDate() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_3,
                SHARE_4,
                SHARE_1
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.AscendingCreationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeDescendingCreationDate() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_1,
                SHARE_4,
                SHARE_3
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.DescendingModificationDate))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeAscendingSize() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_1,
                SHARE_3,
                SHARE_4
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.AscendingFileSize))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeDescendingSize() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_4,
                SHARE_1,
                SHARE_3
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.DescendingFileSize))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeAscendingSender() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_4,
                SHARE_1,
                SHARE_3
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.AscendingSender))
                .isEqualTo(expectedList)
        }

        @Test
        fun sortShareShouldReturnSortedAscendingWithTypeDescendingSender() {
            val inputList = listOf(
                SHARE_1, SHARE_3, SHARE_4
            )

            val expectedList = listOf(
                SHARE_1,
                SHARE_3,
                SHARE_4
            )

            Truth.assertThat(inputList.sortShareBy(OrderListConfigurationType.DescendingSender))
                .isEqualTo(expectedList)
        }
    }
}
