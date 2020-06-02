package com.linagora.android.linshare.view.share

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import arrow.core.Either
import com.linagora.android.linshare.TestApplication
import com.linagora.android.linshare.domain.usecases.autocomplete.GetReceiverSuggestionInteractor
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.Success
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

    private lateinit var shareRecipientsManager: ShareRecipientsManager

    private lateinit var shareFragmentViewModel: ShareFragmentViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        shareRecipientsManager = ShareRecipientsManager(getReceiverSuggestionInteractor)
        shareFragmentViewModel = ShareFragmentViewModel(
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
