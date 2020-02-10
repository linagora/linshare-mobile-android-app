package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.ErrorCode
import java.lang.reflect.Type

class ErrorCodeDeserializer : JsonDeserializer<ErrorCode> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ErrorCode {
        return ErrorCode(json!!.asInt)
    }
}
