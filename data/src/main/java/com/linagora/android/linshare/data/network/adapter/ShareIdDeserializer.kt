package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.share.ShareId
import java.lang.reflect.Type
import java.util.UUID

class ShareIdDeserializer : JsonDeserializer<ShareId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ShareId {
        return ShareId(UUID.fromString(json!!.asString))
    }
}
