package com.foodenak.itpscanner.services

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

/**
 * Created by ITP on 10/5/2015.
 */
class EmptyBody : RequestBody() {
  override fun writeTo(p0: BufferedSink?) {
    // do nothing, what should I write?
  }

  override fun contentType(): MediaType? = type

  override fun contentLength(): Long = 0

  companion object {
    val type = MediaType.parse("application/json");
  }
}