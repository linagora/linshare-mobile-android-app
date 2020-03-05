package com.linagora.android.linshare.utils

import android.os.Build
import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.util.MimeType.APPLICATION_DEFAULT
import com.linagora.android.linshare.util.getMediaType
import com.linagora.android.linshare.util.getMediaTypeFromExtension
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
class MediaTypeExtensionTest {

    @Test
    fun getMediaTypeFromExtensionWithFileName() {

        val fileName = "document.txt"

        assertThat(fileName.getMediaTypeFromExtension()).isEqualTo(APPLICATION_DEFAULT.toMediaType())
    }

    @Test
    fun getMediaTypeFromExtensionWithSpaceFileName() {

        val fileName = "    "

        assertThat(fileName.getMediaTypeFromExtension()).isEqualTo(APPLICATION_DEFAULT.toMediaType())
    }

    @Test
    fun getMediaTypeWithTextMimeType() {

        val fileName = "document.txt"

        val mimeType = "text/plain"

        assertThat(fileName.getMediaType(mimeType)).isEqualTo(mimeType.toMediaType())
    }

    @Test
    fun getMediaTypeWithNullMimeType() {

        val fileName = "document.txt"

        val mimeType = null

        assertThrows<NullPointerException> { fileName.getMediaType(mimeType!!) }
    }

    @Test
    fun getMediaTypeWithSpaceMimeType() {

        val fileName = "document.txt"

        val mimeType = "   "

        assertThat(fileName.getMediaType(mimeType)).isEqualTo(APPLICATION_DEFAULT.toMediaType())
    }
}
