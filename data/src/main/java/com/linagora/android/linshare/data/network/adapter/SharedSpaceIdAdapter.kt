package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceId
import java.lang.reflect.Type
import java.util.UUID

class SharedSpaceIdAdapter : JsonDeserializer<SharedSpaceId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SharedSpaceId {
        return SharedSpaceId(UUID.fromString(json!!.asString))
    }
}
