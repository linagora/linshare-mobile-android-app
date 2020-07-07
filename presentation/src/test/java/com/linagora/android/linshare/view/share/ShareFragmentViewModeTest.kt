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

package com.linagora.android.linshare.view.share

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.test.core.app.ApplicationProvider
import arrow.core.Either
import com.linagora.android.linshare.TestApplication
import com.linagora.android.linshare.domain.usecases.autocomplete.GetReceiverSuggestionInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.util.ConnectionLiveData
import com.linagora.android.linshare.utils.provideFakeCoroutinesDispatcherProvider
import com.linagora.android.linshare.view.widget.ShareRecipientsManager
import com.linagora.android.testshared.ShareFixtures.ADD_RECIPIENT_1_STATE
import com.linagora.android.testshared.ShareFixtures.ADD_RECIPIENT_2_STATE
import com.linagora.android.testshared.ShareFixtures.RECIPIENT_1
import com.linagora.android.testshared.ShareFixtures.RECIPIENT_2
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.internal.verification.Times
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ShareFragmentViewModeTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var viewObserver: Observer<Either<Failure, Success>>

    @Mock
    lateinit var getReceiverSuggestionInteractor: GetReceiverSuggestionInteractor

    private lateinit var internetAvailable: ConnectionLiveData

    private lateinit var shareRecipientsManager: ShareRecipientsManager

    private lateinit var shareFragmentViewModel: ShareFragmentViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        internetAvailable = ConnectionLiveData(ApplicationProvider.getApplicationContext())
        shareRecipientsManager = ShareRecipientsManager(getReceiverSuggestionInteractor)
        shareFragmentViewModel = ShareFragmentViewModel(
            internetAvailable = internetAvailable,
            application = TestApplication(),
            dispatcherProvider = provideFakeCoroutinesDispatcherProvider(TestCoroutineDispatcher()),
            recipientsManager = shareRecipientsManager
        )
    }

    @Test
    fun addRecipientShouldDispatchAddRecipientStateWhenAddRecipientToEmptyRecipients() {
        shareFragmentViewModel.viewState.observeForever(viewObserver)

        shareFragmentViewModel.addRecipient(RECIPIENT_1)

        verify(viewObserver, Times(1)).onChanged(ADD_RECIPIENT_1_STATE)
    }

    @Test
    fun addRecipientShouldNotDispatchStateWhenAddDuplicateRecipient() {
        shareFragmentViewModel.viewState.observeForever(viewObserver)

        shareFragmentViewModel.addRecipient(RECIPIENT_1)
        shareFragmentViewModel.addRecipient(RECIPIENT_1)

        verify(viewObserver, Times(1)).onChanged(ADD_RECIPIENT_1_STATE)
    }

    @Test
    fun addRecipientShouldDispatchStatesWhenAddMultipleRecipient() {
        shareFragmentViewModel.viewState.observeForever(viewObserver)

        shareFragmentViewModel.addRecipient(RECIPIENT_1)
        shareFragmentViewModel.addRecipient(RECIPIENT_2)

        verify(viewObserver, Times(1)).onChanged(ADD_RECIPIENT_1_STATE)
        verify(viewObserver, Times(1)).onChanged(ADD_RECIPIENT_2_STATE)
    }
}
