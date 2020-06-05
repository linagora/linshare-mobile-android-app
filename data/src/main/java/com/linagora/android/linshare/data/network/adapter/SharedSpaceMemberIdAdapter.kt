package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.sharedspace.member.SharedSpaceMemberId
import java.lang.reflect.Type
import java.util.UUID

class SharedSpaceMemberIdAdapter : JsonDeserializer<SharedSpaceMemberId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): SharedSpaceMemberId {
        return SharedSpaceMemberId(UUID.fromString(json!!.asString))
    }
}
