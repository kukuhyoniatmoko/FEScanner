package com.foodenak.foodenak.rest

import android.content.Context
import okhttp3.HttpUrl
import retrofit2.BaseUrl

/**
 * Created by kukuh on 16/02/05.
 */
class ServiceEndPoints(context: Context) : BaseUrl {

  override fun url() = HTTP_URL

  companion object {
    private val END_POINT = "https://www.foodenak.com/api/"
    private val HTTP_URL = HttpUrl.parse(END_POINT)
  }
}
