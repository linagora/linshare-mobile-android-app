package com.linagora.android.linshare.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class LinShareErrorCodeTest {

    companion object {
        private val VALID_ERROR_CODES = listOf(100, 1000, 46011, Int.MAX_VALUE)

        private val INVALID_ERROR_CODES = listOf(-100, -1000, -46011, Int.MIN_VALUE)
    }

    @Test
    fun validateErrorCode() {
        for (errorCode in VALID_ERROR_CODES) {
            try {
                LinShareErrorCode(errorCode)
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }

        for (invalidErrorCode in INVALID_ERROR_CODES) {
            assertThrows<IllegalArgumentException> {
                LinShareErrorCode(invalidErrorCode)
            }
        }
    }
}
