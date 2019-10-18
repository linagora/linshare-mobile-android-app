package com.linagora.android.linshare.network

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class PortTest {

    private val GOOD_PORT = listOf(
        1,
        8080,
        28080,
        80,
        30000,
        65535
    )

    private val BAD_PORT = listOf(
        -1,
        65536,
        80800,
        0
    )

    @Test
    fun testValidPort() {
        for (goodPort in GOOD_PORT) {
            try {
                Port(goodPort)
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }

        for (badPort in BAD_PORT) {
            assertThrows<IllegalArgumentException> {
                Port(badPort)
            }
        }
    }
}
