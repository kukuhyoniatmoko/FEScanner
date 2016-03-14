package com.foodenak.itpscanner.services.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;

/**
 * Created by ITP on 8/14/2015.
 */
public class BooleanDeserializer implements JsonDeserializer<Boolean> {

  @Override
  public Boolean deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.isJsonPrimitive()) {
      JsonPrimitive primitive = json.getAsJsonPrimitive();
      if (primitive.isBoolean()) {
        return primitive.getAsBoolean();
      }
      if (primitive.isNumber()) {
        return primitive.getAsNumber().intValue() > 0;
      }
      if (primitive.isString()) {
        String text = primitive.getAsString();
        switch (text) {
          case "true":
          case "TRUE":
          case "1":
            return Boolean.TRUE;
          case "false":
          case "FALSE":
          case "0":
            return Boolean.FALSE;
        }
      }
    }
    return null;
  }
}
