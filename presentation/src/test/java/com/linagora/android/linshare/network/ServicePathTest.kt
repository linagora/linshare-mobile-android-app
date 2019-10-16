package com.linagora.android.linshare.network

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class ServicePathTest {

    private val GOOD_PATH = listOf(
        "linshare/",
        "linshare/webservice/rest/user/v2",
        "linagora ",
        ""
    )

    private val BAD_PATH = listOf(
        "/linshare/",
        "/linshare/webservice/rest/user/v2",
        "/linagora ",
        "/"
    )

    @Test
    fun testValidServicePath() {
        for (goodPath in GOOD_PATH) {
            try {
                ServicePath(goodPath)
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }

        for (badPath in BAD_PATH) {
            assertThrows<java.lang.IllegalArgumentException> { ServicePath(badPath) }
        }
    }
}
