package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.quota.QuotaSize
import java.lang.reflect.Type

class QuotaSizeDeserializer : JsonDeserializer<QuotaSize> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): QuotaSize {
        return QuotaSize(json!!.asLong)
    }
}
