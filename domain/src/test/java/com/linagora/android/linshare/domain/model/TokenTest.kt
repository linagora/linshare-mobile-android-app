package com.linagora.android.linshare.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class TokenTest {

    private val GOOD_TOKEN = "ZHBoYW1ob2FuZ0BsaW5hZ29yYS5jb206aThqckJ3KTgzNk4="

    private val BAD_TOKENS = listOf(
        "",
        " "
    )

    @Test
    fun testValidToken() {
        try {
            Token(GOOD_TOKEN)
        } catch (exp: IllegalArgumentException) {
            fail(exp)
        }

        for (token in BAD_TOKENS) {
            assertThrows<java.lang.IllegalArgumentException> { Token(token) }
        }
    }
}
