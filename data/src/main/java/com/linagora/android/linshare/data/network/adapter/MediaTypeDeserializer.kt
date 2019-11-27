package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.MediaType
import java.lang.reflect.Type

class MediaTypeDeserializer : JsonDeserializer<MediaType> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MediaType {
        return MediaType.get(json!!.asString)
    }
}
