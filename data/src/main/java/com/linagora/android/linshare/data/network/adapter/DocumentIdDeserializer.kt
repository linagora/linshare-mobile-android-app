package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.linagora.android.linshare.domain.model.document.DocumentId
import java.lang.reflect.Type
import java.util.UUID

class DocumentIdDeserializer : JsonDeserializer<DocumentId> {

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): DocumentId {
        return DocumentId(UUID.fromString(json!!.asString))
    }
}
