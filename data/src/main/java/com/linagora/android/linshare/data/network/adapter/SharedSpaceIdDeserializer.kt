package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.sharespace.ShareSpaceId
import java.lang.reflect.Type
import java.util.UUID

class SharedSpaceIdDeserializer : JsonDeserializer<ShareSpaceId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ShareSpaceId {
        return ShareSpaceId(UUID.fromString(json!!.asString))
    }
}
