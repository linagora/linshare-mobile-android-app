package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.sharedspace.WorkGroupNodeId
import java.lang.reflect.Type
import java.util.UUID

class WorkGroupNodeIdAdapter : JsonDeserializer<WorkGroupNodeId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): WorkGroupNodeId {
        return WorkGroupNodeId(UUID.fromString(json!!.asString))
    }
}
