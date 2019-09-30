package com.linagora.android.linshare.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class UsernameTest {

    private val GOOD_USER_NAME = "alice"

    private val GOOD_USER_NAMES = listOf(
        GOOD_USER_NAME,
        "bob123",
        "bob ",
        "alice.123"
    )

    private val BAD_USER_NAMES = listOf(
        "",
        "    "
    )

    @Test
    fun testValidUsername() {
        for (goodUsername in GOOD_USER_NAMES) {
            try {
                Username(goodUsername)
            } catch (exp: IllegalArgumentException) {
                fail(exp)
            }
        }

        for (badUsername in BAD_USER_NAMES) {
            assertThrows<java.lang.IllegalArgumentException> { Username(badUsername) }
        }
    }
}
