package com.linagora.android.linshare.data.database

import androidx.room.TypeConverter
import com.linagora.android.linshare.domain.model.download.DownloadType
import com.linagora.android.linshare.domain.model.download.EnqueuedDownloadId
import okhttp3.MediaType
import java.util.UUID

class Converters {

    @TypeConverter
    fun fromUUID(uuid: UUID?): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun toUUID(uuidValue: String?): UUID? {
        return uuidValue?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun fromEnqueuedDownloadingId(enqueuedDownloadId: EnqueuedDownloadId): Long {
        return enqueuedDownloadId.value
    }

    @TypeConverter
    fun toEnqueuedDownloadingId(enqueuedId: Long): EnqueuedDownloadId {
        return EnqueuedDownloadId(enqueuedId)
    }

    @TypeConverter
    fun fromMediaType(mediaType: MediaType): String {
        return mediaType.toString()
    }

    @TypeConverter
    fun toMediaType(mediaTypeValue: String): MediaType {
        return MediaType.get(mediaTypeValue)
    }

    @TypeConverter
    fun toDownloadType(downloadType: String): DownloadType {
        return DownloadType.valueOf(downloadType)
    }

    @TypeConverter
    fun fromDownloadType(downloadType: DownloadType): String {
        return downloadType.name
    }
}
