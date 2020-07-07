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

package com.linagora.android.linshare.domain.usecases.quota

import arrow.core.Either
import com.linagora.android.linshare.domain.model.AccountQuota
import com.linagora.android.linshare.domain.model.User
import com.linagora.android.linshare.domain.model.enoughQuotaToUpload
import com.linagora.android.linshare.domain.model.validMaxFileSizeToUpload
import com.linagora.android.linshare.domain.repository.user.QuotaRepository
import com.linagora.android.linshare.domain.repository.user.UserRepository
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.usecases.utils.Success.Loading
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class EnoughAccountQuotaInteractor @Inject constructor(
    private val userRepository: UserRepository,
    private val quotaRepository: QuotaRepository
) {

    operator fun invoke(fileSize: Long): Flow<State<Either<Failure, Success>>> {
        return channelFlow<State<Either<Failure, Success>>> {
            send(State { Either.right(Loading) })
            userRepository.getAuthorizedUser()
                ?.let { user -> enoughQuota(this, user, fileSize) }
                ?: send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
        }
    }

    private suspend fun enoughQuota(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        user: User,
        fileSize: Long
    ) {
        quotaRepository.findQuota(user.quotaUuid)
            ?.let { quota -> validateQuota(producerScope, fileSize, quota) }
            ?: producerScope.send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
    }

    private suspend fun validateQuota(
        producerScope: ProducerScope<State<Either<Failure, Success>>>,
        fileSize: Long,
        quota: AccountQuota
    ) {
        if (!quota.validMaxFileSizeToUpload(fileSize)) {
            producerScope.send(State { Either.left(ExceedMaxFileSize) })
            return
        }

        if (!quota.enoughQuotaToUpload(fileSize)) {
            producerScope.send(State { Either.left(QuotaAccountNoMoreSpaceAvailable) })
            return
        }

        producerScope.send(State { Either.right(ValidAccountQuota) })
    }
}
