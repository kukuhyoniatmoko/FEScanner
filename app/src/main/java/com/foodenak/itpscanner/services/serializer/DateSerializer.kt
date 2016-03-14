package com.foodenak.itpscanner.services.serializer

import com.foodenak.itpscanner.utils.DateFormats
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

/**
 * Created by ITP on 8/15/2015.
 */
class DateSerializer : JsonSerializer<Date> {

    override fun serialize(src: Date, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        try {
            return JsonPrimitive(DateFormats.format(src))
        } catch (e: Exception) {
            return JsonNull.INSTANCE
        }

    }
}
