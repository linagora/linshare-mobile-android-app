package com.linagora.android.linshare.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class PasswordTest {

    private val GOOD_PASSWORD = "asd123"

    private val GOOD_PASSWORDS = listOf(
        GOOD_PASSWORD,
        "password",
        "qwertyui ",
        "4XQrtyu123"
    )

    private val EMPTY_PASSWORDS = listOf(
        "",
        "    "
    )

    @Test
    fun testNotEmptyPassword() {
        for (goodUsername in GOOD_PASSWORDS) {
            try {
                Password(goodUsername)
            } catch (exp: IllegalArgumentException) {
                fail(exp)
            }
        }

        for (badUsername in EMPTY_PASSWORDS) {
            assertThrows<java.lang.IllegalArgumentException> { Password(badUsername) }
        }
    }
}
