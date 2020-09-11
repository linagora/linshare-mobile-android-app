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

package com.linagora.android.linshare.model.order

import com.google.common.truth.Truth
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import org.junit.Test

class OrderTypeNameTest {

    @Test
    fun orderTypeNameShouldGenerateDescendingNameWithInputAscendingName() {
        Truth.assertThat(OrderTypeName.Name.generateNewConfigurationType(OrderListConfigurationType.AscendingName))
                .isEqualTo(OrderListConfigurationType.DescendingName)
    }

    @Test
    fun orderTypeNameShouldGenerateAscendingNameWithInputDescendingName() {
        Truth.assertThat(OrderTypeName.Name.generateNewConfigurationType(OrderListConfigurationType.DescendingName))
            .isEqualTo(OrderListConfigurationType.AscendingName)
    }

    @Test
    fun orderTypeModificationDateShouldGenerateDescendingModificationDateWithInputAscendingModificationDate() {
        Truth.assertThat(OrderTypeName.ModificationDate.generateNewConfigurationType(OrderListConfigurationType.AscendingModificationDate))
            .isEqualTo(OrderListConfigurationType.DescendingModificationDate)
    }

    @Test
    fun orderTypeModificationDateShouldGenerateAscendingModificationDateWithInputDescendingModificationDate() {
        Truth.assertThat(OrderTypeName.ModificationDate.generateNewConfigurationType(OrderListConfigurationType.DescendingModificationDate))
            .isEqualTo(OrderListConfigurationType.AscendingModificationDate)
    }

    @Test
    fun orderTypeCreationDateShouldGenerateDescendingCreationDateWithInputAscendingCreationDate() {
        Truth.assertThat(OrderTypeName.CreationDate.generateNewConfigurationType(OrderListConfigurationType.AscendingCreationDate))
            .isEqualTo(OrderListConfigurationType.DescendingCreationDate)
    }

    @Test
    fun orderTypeCreationDateShouldGenerateAscendingCreationDateWithInputDescendingCreationDate() {
        Truth.assertThat(OrderTypeName.CreationDate.generateNewConfigurationType(OrderListConfigurationType.DescendingCreationDate))
            .isEqualTo(OrderListConfigurationType.AscendingCreationDate)
    }

    @Test
    fun orderTypeFileSizeShouldGenerateDescendingFileSizeWithInputAscendingFileSize() {
        Truth.assertThat(OrderTypeName.FileSize.generateNewConfigurationType(OrderListConfigurationType.AscendingFileSize))
            .isEqualTo(OrderListConfigurationType.DescendingFileSize)
    }

    @Test
    fun orderTypeFileSizeShouldGenerateAscendingFileSizeWithInputDescendingFileSize() {
        Truth.assertThat(OrderTypeName.FileSize.generateNewConfigurationType(OrderListConfigurationType.DescendingFileSize))
            .isEqualTo(OrderListConfigurationType.AscendingFileSize)
    }

    @Test
    fun orderTypeSharedShouldGenerateDescendingSharedWithInputAscendingShared() {
        Truth.assertThat(OrderTypeName.Shared.generateNewConfigurationType(OrderListConfigurationType.AscendingShared))
            .isEqualTo(OrderListConfigurationType.DescendingShared)
    }

    @Test
    fun orderTypeSharedShouldGenerateAscendingSharedWithInputDescendingShared() {
        Truth.assertThat(OrderTypeName.Shared.generateNewConfigurationType(OrderListConfigurationType.DescendingShared))
            .isEqualTo(OrderListConfigurationType.AscendingShared)
    }

    @Test
    fun orderTypeSenderShouldGenerateDescendingSenderWithInputAscendingSender() {
        Truth.assertThat(OrderTypeName.Sender.generateNewConfigurationType(OrderListConfigurationType.AscendingSender))
            .isEqualTo(OrderListConfigurationType.DescendingSender)
    }

    @Test
    fun orderTypeSenderShouldGenerateAscendingSenderWithInputDescendingSender() {
        Truth.assertThat(OrderTypeName.Sender.generateNewConfigurationType(OrderListConfigurationType.DescendingSender))
            .isEqualTo(OrderListConfigurationType.AscendingSender)
    }

    @Test
    fun orderTypeNameShouldGenerateAscendingNameWithDifferentInput() {
        Truth.assertThat(OrderTypeName.Name.generateNewConfigurationType(OrderListConfigurationType.AscendingCreationDate))
            .isEqualTo(OrderListConfigurationType.AscendingName)
    }

    @Test
    fun orderTypeModificationDateShouldGenerateAscendingModificationDateWithDifferentInput() {
        Truth.assertThat(OrderTypeName.ModificationDate.generateNewConfigurationType(OrderListConfigurationType.AscendingName))
            .isEqualTo(OrderListConfigurationType.AscendingModificationDate)
    }

    @Test
    fun orderTypeCreationDateShouldGenerateAscendingCreationDateWithDifferentInput() {
        Truth.assertThat(OrderTypeName.CreationDate.generateNewConfigurationType(OrderListConfigurationType.AscendingName))
            .isEqualTo(OrderListConfigurationType.AscendingCreationDate)
    }

    @Test
    fun orderTypeFileSizeShouldGenerateAscendingFileSizeWithDifferentInput() {
        Truth.assertThat(OrderTypeName.FileSize.generateNewConfigurationType(OrderListConfigurationType.AscendingName))
            .isEqualTo(OrderListConfigurationType.AscendingFileSize)
    }

    @Test
    fun orderTypeSharedShouldGenerateAscendingSharedWithDifferentInput() {
        Truth.assertThat(OrderTypeName.Shared.generateNewConfigurationType(OrderListConfigurationType.AscendingName))
            .isEqualTo(OrderListConfigurationType.AscendingShared)
    }

    @Test
    fun orderTypeSharedShouldGenerateAscendingSenderWithDifferentInput() {
        Truth.assertThat(OrderTypeName.Sender.generateNewConfigurationType(OrderListConfigurationType.AscendingName))
            .isEqualTo(OrderListConfigurationType.AscendingSender)
    }
}
