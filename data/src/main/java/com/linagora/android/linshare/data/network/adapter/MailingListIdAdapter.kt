package com.linagora.android.linshare.data.network.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import com.linagora.android.linshare.domain.model.autocomplete.MailingListId
import java.lang.reflect.Type
import java.util.UUID

class MailingListIdAdapter : JsonDeserializer<MailingListId>, JsonSerializer<MailingListId> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): MailingListId {
        return MailingListId(UUID.fromString(json!!.asString))
    }

    override fun serialize(
        src: MailingListId?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src!!.uuid.toString())
    }
}
