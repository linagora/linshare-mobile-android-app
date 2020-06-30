package com.linagora.android.linshare.util

import android.os.StatFs
import org.slf4j.LoggerFactory

class DeviceStorageStats {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(DeviceStorageStats::class.java)

        // "/data" path is real partition of "/storage/emulated/0".
        const val INTERNAL_ROOT = "/data"

        const val NO_MORE_SPACE = -1L
    }

    fun getDeviceStorageFreeSpace(path: String): Long {
        return runCatching { StatFs(path) }
            .onFailure { LOGGER.error("getDeviceStorageFreeSpace(): ${it.printStackTrace()}") }
            .getOrNull()
            ?.availableBytes
            ?: NO_MORE_SPACE
    }
}
