package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.BaseErrorCode
import com.linagora.android.linshare.domain.model.LinShareErrorCode
import java.lang.reflect.Type

class BaseErrorCodeDeserializer : JsonDeserializer<BaseErrorCode> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BaseErrorCode {
        return LinShareErrorCode(json!!.asInt)
    }
}
