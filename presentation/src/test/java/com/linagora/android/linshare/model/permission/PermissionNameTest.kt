package com.linagora.android.linshare.model.permission

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class PermissionNameTest {
    companion object {
        private val VALID_PERMISSION_NAMES = listOf(
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.READ_CONTACTS"
        )

        private val INVALID_PERMISSION_NAMES = listOf("", " ")
    }

    @Test
    fun validatePermissionName() {
        for (name in VALID_PERMISSION_NAMES) {
            try {
                PermissionName(name)
            } catch (throwable: Throwable) {
                fail(throwable)
            }
        }

        for (invalid_name in INVALID_PERMISSION_NAMES) {
            assertThrows<IllegalArgumentException> {
                PermissionName(invalid_name)
            }
        }
    }
}
