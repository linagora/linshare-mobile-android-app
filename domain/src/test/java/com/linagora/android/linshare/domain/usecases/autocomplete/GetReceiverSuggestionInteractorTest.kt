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

package com.linagora.android.linshare.domain.usecases.autocomplete

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompletePattern
import com.linagora.android.linshare.domain.model.autocomplete.AutoCompleteType
import com.linagora.android.linshare.domain.model.contact.SimpleContact
import com.linagora.android.linshare.domain.model.contact.toAutoCompleteResult
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_RESULTS
import com.linagora.android.testshared.AutoCompleteFixtures.USER_AUTOCOMPLETE_STATE
import com.linagora.android.testshared.ShareFixtures.CONTACT_SUGGESTION_RESULTS
import com.linagora.android.testshared.ShareFixtures.CONTACT_SUGGESTION_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class GetReceiverSuggestionInteractorTest {

    @Mock
    lateinit var getAutoCompleteSharingInteractor: GetAutoCompleteSharingInteractor

    @Mock
    lateinit var getContactSuggestionInteractor: GetContactSuggestionInteractor

    private lateinit var getReceiverSuggestionInteractor: GetReceiverSuggestionInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        getReceiverSuggestionInteractor = GetReceiverSuggestionInteractor(
            getContactSuggestionInteractor,
            getAutoCompleteSharingInteractor)
    }

    @Test
    fun getReceiverSuggestionShouldReturnMatchedListCombineFromLinShareAndDevice() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")
        val expectedSuggestion = USER_AUTOCOMPLETE_RESULTS
            .plus(CONTACT_SUGGESTION_RESULTS.map(SimpleContact::toAutoCompleteResult))

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(2000)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern, AutoCompleteType.SHARING))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())
            .last()

        assertThat(lastResult).isEqualTo(Either.right(AutoCompleteViewState(expectedSuggestion)))
    }

    @Test
    fun getReceiverSuggestionShouldReturnPartialMatchedListFromLinShare() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")
        val expectedPartialSuggestion = USER_AUTOCOMPLETE_RESULTS

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(2000)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern, AutoCompleteType.SHARING))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(500)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[lastResult.size - 2]).isEqualTo(Either.right(AutoCompleteViewState(expectedPartialSuggestion)))
    }

    @Test
    fun getReceiverSuggestionShouldReturnPartialMatchedListFromDevice() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")
        val expectedPartialSuggestion = CONTACT_SUGGESTION_RESULTS
            .map(SimpleContact::toAutoCompleteResult)

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(200)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern, AutoCompleteType.SHARING))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[lastResult.size - 2]).isEqualTo(Either.right(AutoCompleteViewState(expectedPartialSuggestion)))
    }

    @Test
    fun getReceiverSuggestionShouldReturnLoadingWhenQueryFromDeviceButNoResultQueryFromLinShare() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(200)
                    emit(CONTACT_SUGGESTION_STATE)
                } }

        `when`(getAutoCompleteSharingInteractor(pattern, AutoCompleteType.SHARING))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(Either.left(AutoCompleteNoResult(pattern)))
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[0]).isEqualTo(LOADING_STATE)
    }

    @Test
    fun getReceiverSuggestionShouldReturnLoadingWhenQueryFromLinShareButNoResultQueryFromDevice() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(Either.left(AutoCompleteNoResult(pattern)))
                } }

        `when`(getAutoCompleteSharingInteractor(pattern, AutoCompleteType.SHARING))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(100)
                    emit(USER_AUTOCOMPLETE_STATE)
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())

        assertThat(lastResult).hasSize(3)
        assertThat(lastResult[0]).isEqualTo(LOADING_STATE)
    }

    @Test
    fun getReceiverSuggestionShouldReturnNoResultWhenNoResultQueryFromLinShareAndQueryFromDeviceFail() = runBlockingTest {
        val pattern = AutoCompletePattern("bar")

        `when`(getContactSuggestionInteractor(pattern))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(1000)
                    emit(Either.left(ContactSuggestionFailure(RuntimeException())))
                } }

        `when`(getAutoCompleteSharingInteractor(pattern, AutoCompleteType.SHARING))
            .thenAnswer {
                flow {
                    emit(LOADING_STATE)
                    delay(100)
                    emit(Either.left(AutoCompleteNoResult(pattern)))
                } }

        val lastResult = getReceiverSuggestionInteractor(pattern)
            .toList(ArrayList())
            .last()

        assertThat(lastResult).isEqualTo(Either.left(ReceiverSuggestionNoResult(pattern)))
    }
}
