package com.linagora.android.linshare.domain.usecases

import arrow.core.Either
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.usecases.upload.UploadErrorHandler
import com.linagora.android.linshare.domain.usecases.upload.UploadException
import com.linagora.android.linshare.domain.usecases.utils.Failure
import com.linagora.android.linshare.domain.usecases.utils.State
import com.linagora.android.linshare.domain.usecases.utils.Success
import com.linagora.android.linshare.domain.utils.ErrorResponseConstant.FILE_NOT_FOUND
import com.linagora.android.testshared.TestFixtures.State.INIT_STATE
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class InteractorHandlerTest {

    companion object {
        const val WRONG_VALUE_REQUEST = 7

        const val VALID_VALUE_REQUEST = 3
    }

    @Mock
    lateinit var producerScope: ProducerScope<State<Either<Failure, Success>>>

    private lateinit var interactorHandler: InteractorHandler

    private fun fakeInteract(value: Int): Int {
        require(value < 5) { "invalid value" }
        return value
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        interactorHandler = InteractorHandler()
    }

    @Nested
    inner class CommonCases {

        @Test
        fun handleShouldReturnASuccessRequest() = runBlockingTest {
            assertThat(interactorHandler.handle(
                    execution = { fakeInteract(VALID_VALUE_REQUEST) }))
                .isEqualTo(VALID_VALUE_REQUEST)
        }

        @Test
        fun handleShouldThrownAnExceptionOnFailure() {
            assertThrows<IllegalArgumentException> {
                runBlockingTest { interactorHandler.handle(execution = { fakeInteract(7) }) }
            }
        }

        @Test
        fun handleShouldThrownAnExceptionWhenThrowExceptionOnFailure() {
            assertThrows<RuntimeException> { runBlockingTest {
                interactorHandler.handle(
                    execution = { fakeInteract(WRONG_VALUE_REQUEST) },
                    onCatch = {
                        if (it is IllegalArgumentException) {
                            throw RuntimeException("failed request")
                        }
                    })
            } }
        }
    }

    @Nested
    inner class UploadCases {

        private lateinit var uploadErrorHandler: UploadErrorHandler

        private fun fakeUpload(value: Int): Int {
            if (value > 5) {
                throw UploadException(FILE_NOT_FOUND)
            }
            return value
        }

        @Test
        fun handleShouldReturnASuccessRequestWhenUploadSuccessWithUploadErrorHandler() = runBlockingTest {
            produce<State<Either<Failure, Success>>> {
                uploadErrorHandler = UploadErrorHandler(this)
            }

            assertThat(interactorHandler.handle(
                    execution = { fakeUpload(VALID_VALUE_REQUEST) },
                    onCatch = { uploadErrorHandler(it) }))
                .isEqualTo(VALID_VALUE_REQUEST)
        }

        @Test
        fun handleShouldBeHandleOnThrowWhenUploadSuccessWithUploadErrorHandler() = runBlockingTest {
            val states = channelFlow<State<Either<Failure, Success>>> {
                uploadErrorHandler = UploadErrorHandler(this)

                interactorHandler.handle(
                    execution = { fakeUpload(WRONG_VALUE_REQUEST) },
                    onCatch = { uploadErrorHandler(it) }
                )
            }.toList(ArrayList())

            assertThat(states).hasSize(1)
            assertThat(states[0](INIT_STATE)).isEqualTo(Either.left(Failure.Error))
        }
    }
}
