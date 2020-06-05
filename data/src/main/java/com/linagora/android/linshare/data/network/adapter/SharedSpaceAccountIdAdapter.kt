package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceAccountId
import java.lang.reflect.Type
import java.util.UUID

class SharedSpaceAccountIdAdapter : JsonDeserializer<SharedSpaceAccountId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SharedSpaceAccountId {
        return SharedSpaceAccountId(UUID.fromString(json!!.asString))
    }
}
