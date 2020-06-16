package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.sharedspace.SharedSpaceRoleId
import java.lang.reflect.Type
import java.util.UUID

class SharedSpaceRoleIdAdapter : JsonDeserializer<SharedSpaceRoleId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SharedSpaceRoleId {
        return SharedSpaceRoleId(UUID.fromString(json!!.asString))
    }
}
