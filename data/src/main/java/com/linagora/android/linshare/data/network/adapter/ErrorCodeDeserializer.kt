package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import java.lang.reflect.Type

class ErrorCodeDeserializer : JsonDeserializer<LinShareErrorCode> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LinShareErrorCode {
        return LinShareErrorCode(json!!.asInt)
    }
}
