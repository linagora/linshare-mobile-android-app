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

package com.linagora.android.testshared.repository.credential

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.Credential
import com.linagora.android.linshare.domain.repository.CredentialRepository
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL
import com.linagora.android.testshared.TestFixtures.Credentials.CREDENTIAL2
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Test

abstract class CredentialRepositoryContract {

    abstract val credentialRepository: CredentialRepository

    @Test
    open fun persistsCredentialShouldNotSaveDuplicateCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).hasSize(1)
            assertThat(credentials).containsExactly(CREDENTIAL)
        }
    }

    @Test
    open fun persistsCredentialShouldSaveCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).containsExactly(CREDENTIAL)
        }
    }

    @Test
    open fun persistsCredentialShouldUpdateCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            val credentials = credentialRepository.getAllCredential()

            assertThat(credentials).contains(CREDENTIAL2)
        }
    }

    @Test
    open fun persistCredentialShouldSetCurrentCredentialForTheLastSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            val currentCredential = credentialRepository.getCurrentCredential()

            assertThat(currentCredential).isEqualTo(CREDENTIAL2)
        }
    }

    @Test
    open fun removeCredentialShouldRemoveExactlyCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.removeCredential(CREDENTIAL)

            val credential = credentialRepository.getAllCredential()

            assertThat(credential).containsExactly(CREDENTIAL2)
        }
    }

    @Test
    open fun setCurrentCredentialShouldSuccess() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.setCurrentCredential(CREDENTIAL2)

            val credential = credentialRepository.getCurrentCredential()

            assertThat(credential).isEqualTo(CREDENTIAL2)
        }
    }

    @Test
    open fun setCurrentCredentialShouldFailedWithNotSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            assertThat(credentialRepository.setCurrentCredential(CREDENTIAL2))
                .isFalse()
        }
    }

    @Test
    open fun setCurrentCredentialShouldNotUpdateWithNotSavedCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)

            credentialRepository.setCurrentCredential(CREDENTIAL2)

            val credential = credentialRepository.getCurrentCredential()

            assertThat(credential).isEqualTo(CREDENTIAL)
        }
    }

    @Test
    fun getCurrentCredentialShouldReturnEmptyWithNoSavedCredential() {
        runBlockingTest {
            assertThat(credentialRepository.getCurrentCredential())
                .isNull()
        }
    }

    @Test
    fun getCurrentCredentialShouldReturnEmptyAfterClearCredential() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.clearCredential()

            assertThat(credentialRepository.getCurrentCredential())
                .isNull()
        }
    }

    @Test
    fun getAllCredentialShouldReturnEmptyWithNoneSavedCredential() {
        runBlockingTest {
            assertThat(credentialRepository.getAllCredential())
                .isEqualTo(emptySet<Credential>())
        }
    }

    @Test
    fun getAllCredentialShouldReturnEmptyAfterClearingCredentials() {
        runBlockingTest {
            credentialRepository.persistsCredential(CREDENTIAL)
            credentialRepository.persistsCredential(CREDENTIAL2)

            credentialRepository.clearCredential()

            assertThat(credentialRepository.getAllCredential())
                .isEqualTo(emptySet<Credential>())
        }
    }
}
