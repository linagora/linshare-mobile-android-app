package com.linagora.android.linshare.domain.usecases.share

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.testshared.ShareFixtures.SHARE_1
import com.linagora.android.testshared.ShareFixtures.SHARE_2
import com.linagora.android.testshared.ShareFixtures.SHARE_CREATION_1
import com.linagora.android.testshared.ShareFixtures.SHARE_STATE_WITH_MULTIPLE_SHARES
import com.linagora.android.testshared.ShareFixtures.SHARE_STATE_WITH_ONE_SHARE
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import com.linagora.android.testshared.TestFixtures.State.LOADING_STATE
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class ShareDocumentInteractorTest {

    @Mock
    lateinit var documentRepository: DocumentRepository

    private lateinit var shareDocumentInteractor: ShareDocumentInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        shareDocumentInteractor = ShareDocumentInteractor(documentRepository)
    }

    @Test
    fun shareShouldReturnShareWithOneRecipient() = runBlockingTest {
        `when`(documentRepository.share(SHARE_CREATION_1))
            .thenAnswer { listOf(SHARE_1) }

        assertThat(shareDocumentInteractor(SHARE_CREATION_1)
                .map { it(INIT_STATE) }
                .toList(ArrayList()))
            .containsExactly(LOADING_STATE, SHARE_STATE_WITH_ONE_SHARE)
    }

    @Test
    fun shareShouldReturnSharesWithMultiRecipient() = runBlockingTest {
        `when`(documentRepository.share(SHARE_CREATION_1))
            .thenAnswer { listOf(SHARE_1, SHARE_2) }

        assertThat(shareDocumentInteractor(SHARE_CREATION_1)
            .map { it(INIT_STATE) }
            .toList(ArrayList()))
            .containsExactly(LOADING_STATE, SHARE_STATE_WITH_MULTIPLE_SHARES)
    }
}
