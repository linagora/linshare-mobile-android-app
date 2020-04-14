package com.linagora.android.linshare.view.share

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.TestApplication
import com.linagora.android.linshare.domain.usecases.autocomplete.GetAutoCompleteSharingInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.utils.provideFakeCoroutinesDispatcherProvider
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
    lateinit var getAutoCompleteSharingInteractor: GetAutoCompleteSharingInteractor

    private lateinit var shareFragmentViewModel: ShareFragmentViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        shareFragmentViewModel = ShareFragmentViewModel(
            application = TestApplication(),
            dispatcherProvider = provideFakeCoroutinesDispatcherProvider(TestCoroutineDispatcher()),
            getAutoCompleteSharingInteractor = getAutoCompleteSharingInteractor
        )
    }

    @Test
    fun addRecipientShouldAddRecipientToEmptyRecipients() {
        shareFragmentViewModel.addRecipient(RECIPIENT_1)

        assertThat(shareFragmentViewModel.recipients.value)
            .containsExactly(RECIPIENT_1)
    }

    @Test
    fun addRecipientShouldNotAddDuplicateRecipient() {
        shareFragmentViewModel.addRecipient(RECIPIENT_1)
        shareFragmentViewModel.addRecipient(RECIPIENT_1)

        assertThat(shareFragmentViewModel.recipients.value)
            .hasSize(1)
        assertThat(shareFragmentViewModel.recipients.value?.first())
            .isEqualTo(RECIPIENT_1)
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
    fun addRecipientShouldAddMultipleRecipient() {
        shareFragmentViewModel.addRecipient(RECIPIENT_1)
        shareFragmentViewModel.addRecipient(RECIPIENT_2)

        assertThat(shareFragmentViewModel.recipients.value)
            .containsExactly(RECIPIENT_1, RECIPIENT_2)
    }

    @Test
    fun addRecipientShouldDispatchStatesWhenAddMultipleRecipient() {
        shareFragmentViewModel.viewState.observeForever(viewObserver)

        shareFragmentViewModel.addRecipient(RECIPIENT_1)
        shareFragmentViewModel.addRecipient(RECIPIENT_2)

        verify(viewObserver, Times(1)).onChanged(ADD_RECIPIENT_1_STATE)
        verify(viewObserver, Times(1)).onChanged(ADD_RECIPIENT_2_STATE)
    }

    @Test
    fun removeRecipientShouldNotErrorWhenRecipientEmpty() {
        shareFragmentViewModel.removeRecipient(RECIPIENT_1)

        assertThat(shareFragmentViewModel.recipients.value).isEmpty()
    }

    @Test
    fun removeRecipientShouldRemoveRecipient() {
        shareFragmentViewModel.addRecipient(RECIPIENT_1)
        shareFragmentViewModel.removeRecipient(RECIPIENT_1)

        assertThat(shareFragmentViewModel.recipients.value).isEmpty()
    }

    @Test
    fun removeRecipientShouldNotRemoveNotMatchedRecipient() {
        shareFragmentViewModel.addRecipient(RECIPIENT_1)
        shareFragmentViewModel.removeRecipient(RECIPIENT_2)

        assertThat(shareFragmentViewModel.recipients.value).containsExactly(RECIPIENT_1)
    }
}
