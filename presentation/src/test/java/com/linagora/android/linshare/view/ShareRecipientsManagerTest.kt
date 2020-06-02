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
