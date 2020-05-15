package com.linagora.android.linshare.domain.model

import com.google.common.truth.Truth.assertThat
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRole
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleName
import com.linagora.android.linshare.domain.model.sharedspace.canUpload
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class SharedSpaceRoleTest {

    @Nested
    inner class Uploads {

        @Test
        fun canUploadShouldReturnTrueWhenAdmin() {
            val role = SharedSpaceRole(UUID.randomUUID(), SharedSpaceRoleName.ADMIN)
            assertThat(role.canUpload()).isTrue()
        }

        @Test
        fun canUploadShouldReturnTrueWhenWriter() {
            val role = SharedSpaceRole(UUID.randomUUID(), SharedSpaceRoleName.WRITER)
            assertThat(role.canUpload()).isTrue()
        }

        @Test
        fun canUploadShouldReturnTrueWhenContributor() {
            val role = SharedSpaceRole(UUID.randomUUID(), SharedSpaceRoleName.CONTRIBUTOR)
            assertThat(role.canUpload()).isTrue()
        }

        @Test
        fun canUploadShouldReturnFalseWhenReader() {
            val role = SharedSpaceRole(UUID.randomUUID(), SharedSpaceRoleName.READER)
            assertThat(role.canUpload()).isFalse()
        }
    }
}
