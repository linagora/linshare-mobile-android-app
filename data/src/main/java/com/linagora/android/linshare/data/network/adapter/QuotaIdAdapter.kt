package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.quota.QuotaId
import java.lang.reflect.Type
import java.util.UUID

class QuotaIdAdapter : JsonDeserializer<QuotaId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): QuotaId {
        return QuotaId(UUID.fromString(json!!.asString))
    }
}
