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

package com.linagora.android.linshare.data.repository.document

import com.linagora.android.linshare.data.datasource.DocumentDataSource
import com.linagora.android.linshare.domain.model.copy.CopyRequest
import com.linagora.android.linshare.domain.model.document.Document
import com.linagora.android.linshare.domain.model.document.DocumentId
import com.linagora.android.linshare.domain.model.document.DocumentRenameRequest
import com.linagora.android.linshare.domain.model.document.DocumentRequest
import com.linagora.android.linshare.domain.model.search.QueryString
import com.linagora.android.linshare.domain.model.share.Share
import com.linagora.android.linshare.domain.model.share.ShareRequest
import com.linagora.android.linshare.domain.model.upload.OnTransfer
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import javax.inject.Inject

class DocumentRepositoryImp @Inject constructor(
    private val linShareDocumentDataSource: DocumentDataSource
) : DocumentRepository {

    override suspend fun upload(
        documentRequest: DocumentRequest,
        onTransfer: OnTransfer
    ): Document {
        return linShareDocumentDataSource.upload(documentRequest, onTransfer)
    }

    override suspend fun getAll(): List<Document> {
        return linShareDocumentDataSource.getAll()
    }

    override suspend fun get(documentId: DocumentId): Document {
        return linShareDocumentDataSource.get(documentId)
    }

    override suspend fun remove(documentId: DocumentId): Document {
        return linShareDocumentDataSource.remove(documentId)
    }

    override suspend fun search(query: QueryString): List<Document> {
        return linShareDocumentDataSource.search(query)
    }

    override suspend fun share(shareRequest: ShareRequest): List<Share> {
        return linShareDocumentDataSource.share(shareRequest)
    }

    override suspend fun copy(copyRequest: CopyRequest): List<Document> {
        return linShareDocumentDataSource.copy(copyRequest)
    }

    override suspend fun renameDocument(documentId: DocumentId, documentRenameRequest: DocumentRenameRequest): Document {
        return linShareDocumentDataSource.renameDocument(documentId, documentRenameRequest)
    }
}
