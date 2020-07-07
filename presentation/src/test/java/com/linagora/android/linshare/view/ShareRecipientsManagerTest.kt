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

package com.linagora.android.linshare.view

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.usecases.autocomplete.GetReceiverSuggestionInteractor
import com.linagora.android.linshare.view.widget.ShareRecipientsManager
import com.linagora.android.testshared.ShareFixtures
import com.linagora.android.testshared.ShareFixtures.MAILING_LIST_1
import com.linagora.android.testshared.ShareFixtures.MAILING_LIST_2
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class ShareRecipientsManagerTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Mock
    lateinit var getReceiverSuggestionInteractor: GetReceiverSuggestionInteractor

    private lateinit var shareRecipientsManager: ShareRecipientsManager

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        shareRecipientsManager = ShareRecipientsManager(getReceiverSuggestionInteractor)
    }

    @Test
    fun addRecipientShouldAddRecipientToEmptyRecipients() {
        shareRecipientsManager.addRecipient(ShareFixtures.RECIPIENT_1)

        assertThat(shareRecipientsManager.recipients.value)
            .containsExactly(ShareFixtures.RECIPIENT_1)
    }

    @Test
    fun addRecipientShouldNotAddDuplicateRecipient() {
        shareRecipientsManager.addRecipient(ShareFixtures.RECIPIENT_1)
        shareRecipientsManager.addRecipient(ShareFixtures.RECIPIENT_1)

        assertThat(shareRecipientsManager.recipients.value)
            .hasSize(1)
        assertThat(shareRecipientsManager.recipients.value?.first())
            .isEqualTo(ShareFixtures.RECIPIENT_1)
    }

    @Test
    fun addRecipientShouldAddMultipleRecipient() {
        shareRecipientsManager.addRecipient(ShareFixtures.RECIPIENT_1)
        shareRecipientsManager.addRecipient(ShareFixtures.RECIPIENT_2)

        assertThat(shareRecipientsManager.recipients.value)
            .containsExactly(ShareFixtures.RECIPIENT_1, ShareFixtures.RECIPIENT_2)
    }

    @Test
    fun removeRecipientShouldNotErrorWhenRecipientEmpty() {
        shareRecipientsManager.removeRecipient(ShareFixtures.RECIPIENT_1)

        assertThat(shareRecipientsManager.recipients.value).isEmpty()
    }

    @Test
    fun removeRecipientShouldRemoveRecipient() {
        shareRecipientsManager.addRecipient(ShareFixtures.RECIPIENT_1)
        shareRecipientsManager.removeRecipient(ShareFixtures.RECIPIENT_1)

        assertThat(shareRecipientsManager.recipients.value).isEmpty()
    }

    @Test
    fun removeRecipientShouldNotRemoveNotMatchedRecipient() {
        shareRecipientsManager.addRecipient(ShareFixtures.RECIPIENT_1)
        shareRecipientsManager.removeRecipient(ShareFixtures.RECIPIENT_2)

        assertThat(shareRecipientsManager.recipients.value).containsExactly(ShareFixtures.RECIPIENT_1)
    }

    @Test
    fun addMailingListShouldAddMailingListToEmptyMailingLists() {
        shareRecipientsManager.addMailingList(MAILING_LIST_1)

        assertThat(shareRecipientsManager.mailingLists.value)
            .containsExactly(MAILING_LIST_1)
    }

    @Test
    fun addMailingListShouldNotAddDuplicateMailingList() {
        shareRecipientsManager.addMailingList(MAILING_LIST_1)
        shareRecipientsManager.addMailingList(MAILING_LIST_1)

        assertThat(shareRecipientsManager.mailingLists.value)
            .hasSize(1)
        assertThat(shareRecipientsManager.mailingLists.value?.first())
            .isEqualTo(MAILING_LIST_1)
    }

    @Test
    fun addMailingListShouldAddMultipleMailingList() {
        shareRecipientsManager.addMailingList(MAILING_LIST_1)
        shareRecipientsManager.addMailingList(MAILING_LIST_2)

        assertThat(shareRecipientsManager.mailingLists.value)
            .containsExactly(MAILING_LIST_1, MAILING_LIST_2)
    }

    @Test
    fun removeMailingListShouldNotErrorWhenMailingListsEmpty() {
        shareRecipientsManager.removeMailingList(MAILING_LIST_1)

        assertThat(shareRecipientsManager.mailingLists.value).isEmpty()
    }

    @Test
    fun removeMailingListShouldRemoveMailingList() {
        shareRecipientsManager.addMailingList(MAILING_LIST_1)
        shareRecipientsManager.removeMailingList(MAILING_LIST_1)

        assertThat(shareRecipientsManager.mailingLists.value).isEmpty()
    }

    @Test
    fun removeMailingListShouldNotRemoveNotMatchedMailingList() {
        shareRecipientsManager.addMailingList(MAILING_LIST_1)
        shareRecipientsManager.removeMailingList(MAILING_LIST_2)

        assertThat(shareRecipientsManager.mailingLists.value)
            .containsExactly(MAILING_LIST_1)
    }
}
