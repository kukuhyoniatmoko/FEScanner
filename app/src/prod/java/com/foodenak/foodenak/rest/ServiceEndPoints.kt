package com.foodenak.foodenak.rest

import android.content.Context
import okhttp3.HttpUrl

/**
 * Created by kukuh on 16/02/05.
 */
class ServiceEndPoints(context: Context) {

  fun url() = HTTP_URL

  companion object {
    private val END_POINT = "https://www.foodenak.com/api/"
    private val HTTP_URL = HttpUrl.parse(END_POINT)
  }
}
