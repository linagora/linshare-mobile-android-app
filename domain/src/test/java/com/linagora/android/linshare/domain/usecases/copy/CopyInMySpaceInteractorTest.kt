package com.linagora.android.linshare.domain.usecases.copy

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.repository.document.DocumentRepository
import com.linagora.android.testshared.CopyFixtures.COPY_REQUEST_1
import com.linagora.android.testshared.CopyFixtures.COPY_SUCCESS_STATE_1
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class CopyInMySpaceInteractorTest {

    @Mock
    lateinit var documentRepository: DocumentRepository

    private lateinit var copyInMySpaceInteractor: CopyInMySpaceInteractor

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        copyInMySpaceInteractor = CopyInMySpaceInteractor(documentRepository)
    }

    @Test
    fun copyShouldSuccess() = runBlockingTest {
        `when`(documentRepository.copy(COPY_REQUEST_1))
            .thenAnswer { listOf(DOCUMENT) }

        val copyStates = copyInMySpaceInteractor(COPY_REQUEST_1)
            .toList(ArrayList())

        assertThat(copyStates).hasSize(1)
        assertThat(copyStates[0](INIT_STATE))
            .isEqualTo(COPY_SUCCESS_STATE_1)
    }
}
