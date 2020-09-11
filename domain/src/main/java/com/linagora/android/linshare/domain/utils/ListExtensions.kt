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

import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.order.OrderListConfigurationType
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceNodeNested
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupDocument
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupFolder
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNode

fun List<WorkGroupNode>.sortBy(orderConfigType: OrderListConfigurationType): List<WorkGroupNode> {
    return when (orderConfigType) {
        OrderListConfigurationType.AscendingModificationDate -> this.sortedBy { it.modificationDate }
        OrderListConfigurationType.DescendingModificationDate -> this.sortedByDescending { it.modificationDate }
        OrderListConfigurationType.AscendingCreationDate -> this.sortedBy { it.creationDate }
        OrderListConfigurationType.DescendingCreationDate -> this.sortedByDescending { it.creationDate }
        OrderListConfigurationType.AscendingName -> this.sortedBy { it.name }
        OrderListConfigurationType.DescendingName -> this.sortedByDescending { it.name }
        OrderListConfigurationType.AscendingFileSize, OrderListConfigurationType.DescendingFileSize -> this.sortByFileSize(orderConfigType)
        else -> this.sortedBy { it.modificationDate }
    }
}

private fun List<WorkGroupNode>.sortByFileSize(orderConfigType: OrderListConfigurationType): List<WorkGroupNode> {
    val workGroupDocumentList = this.filterIsInstance<WorkGroupDocument>()
    return this.takeIf { orderConfigType == OrderListConfigurationType.AscendingFileSize || orderConfigType == OrderListConfigurationType.DescendingFileSize }
        ?.let {
            this.takeIf { orderConfigType == OrderListConfigurationType.AscendingFileSize }
                ?.let {
                    workGroupDocumentList.sortedBy { it.size }
                        .plus(this.filterIsInstance<WorkGroupFolder>())
                }
                ?: workGroupDocumentList.sortedByDescending { it.size }
                    .let { documentList ->
                        this.filterIsInstance<WorkGroupFolder>()
                            .plus(documentList)
                    }
        }
        ?: this
}

fun List<SharedSpaceNodeNested>.sortSharedSpaceNodeNestedBy(orderConfigType: OrderListConfigurationType): List<SharedSpaceNodeNested> {
    return when (orderConfigType) {
        OrderListConfigurationType.AscendingModificationDate -> this.sortedBy { it.modificationDate }
        OrderListConfigurationType.DescendingModificationDate -> this.sortedByDescending { it.modificationDate }
        OrderListConfigurationType.AscendingCreationDate -> this.sortedBy { it.creationDate }
        OrderListConfigurationType.DescendingCreationDate -> this.sortedByDescending { it.creationDate }
        OrderListConfigurationType.AscendingName -> this.sortedBy { it.name }
        OrderListConfigurationType.DescendingName -> this.sortedByDescending { it.name }
        else -> this.sortedBy { it.modificationDate }
    }
}

fun List<Document>.sortDocumentBy(orderConfigType: OrderListConfigurationType): List<Document> {
    return when (orderConfigType) {
        OrderListConfigurationType.AscendingModificationDate -> this.sortedBy { it.modificationDate }
        OrderListConfigurationType.DescendingModificationDate -> this.sortedByDescending { it.modificationDate }
        OrderListConfigurationType.AscendingCreationDate -> this.sortedBy { it.creationDate }
        OrderListConfigurationType.DescendingCreationDate -> this.sortedByDescending { it.creationDate }
        OrderListConfigurationType.AscendingName -> this.sortedBy { it.name }
        OrderListConfigurationType.DescendingName -> this.sortedByDescending { it.name }
        OrderListConfigurationType.AscendingFileSize -> this.sortedBy { it.size }
        OrderListConfigurationType.DescendingFileSize -> this.sortedByDescending { it.size }
        OrderListConfigurationType.AscendingShared, OrderListConfigurationType.DescendingShared -> sortDocumentByShared(orderConfigType)
        else -> this.sortedBy { it.modificationDate }
    }
}

private fun List<Document>.sortDocumentByShared(orderConfigType: OrderListConfigurationType): List<Document> {
    return takeIf { orderConfigType == OrderListConfigurationType.AscendingShared || orderConfigType == OrderListConfigurationType.DescendingShared }
        ?.let { documents -> orderConfigType.takeIf { it.isAscending() }
            ?.let { documents.sortedWith(compareBy(Document::shared).thenBy(Document::modificationDate)) }
            ?: documents.sortedWith(compareByDescending(Document::shared).thenBy(Document::modificationDate)) }
        ?: this
}

fun List<Share>.sortShareBy(orderConfigType: OrderListConfigurationType): List<Share> {
    return when (orderConfigType) {
        OrderListConfigurationType.AscendingModificationDate -> this.sortedBy { it.modificationDate }
        OrderListConfigurationType.DescendingModificationDate -> this.sortedByDescending { it.modificationDate }
        OrderListConfigurationType.AscendingCreationDate -> this.sortedBy { it.creationDate }
        OrderListConfigurationType.DescendingCreationDate -> this.sortedByDescending { it.creationDate }
        OrderListConfigurationType.AscendingName -> this.sortedBy { it.name }
        OrderListConfigurationType.DescendingName -> this.sortedByDescending { it.name }
        OrderListConfigurationType.AscendingFileSize -> this.sortedBy { it.size }
        OrderListConfigurationType.DescendingFileSize -> this.sortedByDescending { it.size }
        OrderListConfigurationType.AscendingSender -> this.sortedBy { it.sender.firstName }
        OrderListConfigurationType.DescendingSender -> this.sortedByDescending { it.sender.firstName }
        else -> this.sortedBy { it.modificationDate }
    }
}
