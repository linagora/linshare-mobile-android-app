package com.linagora.android.linshare.domain.model.download

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class EnqueuedDownloadIdTest {

    private val GOOD_IDS = listOf<Long>(
        1,
        8080,
        28080,
        80,
        30000,
        65535
    )

    private val BAD_IDS = listOf<Long>(
        -1,
        -65536,
        -80800
    )

    @Test
    fun testValidEnqueuedDownloadId() {
        for (goodId in GOOD_IDS) {
            try {
                EnqueuedDownloadId(goodId)
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }

        for (badId in BAD_IDS) {
            assertThrows<IllegalArgumentException> {
                EnqueuedDownloadId(badId)
            }
        }
    }
}
