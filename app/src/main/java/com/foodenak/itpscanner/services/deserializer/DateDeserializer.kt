package com.foodenak.itpscanner.services.deserializer

import com.foodenak.itpscanner.utils.DateFormats
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type
import java.text.ParseException
import java.util.*

/**
 * Created by ITP on 8/15/2015.
 */
class DateDeserializer : JsonDeserializer<Date> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Date? {
        if (json.isJsonPrimitive) {
            val time = json.asString
            try {
                return DateFormats.parse(time)
            } catch (e: ParseException) {
                try {
                    return DateFormats.parseShort(time)
                } catch (e1: ParseException) {
                    throw JsonParseException("wrong format = " + time, e1)
                }

            }

        }
        return null
    }
}
