package com.linagora.android.linshare.model.parcelable

import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT
import com.linagora.android.testshared.TestFixtures.Documents.DOCUMENT_2
import okhttp3.internal.immutableListOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class DocumentParcelableTest {

    @Test
    fun toParcelableShouldSuccess() {
        for (document in immutableListOf(DOCUMENT, DOCUMENT_2)) {
            try {
                document.toParcelable()
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }
    }
}
