package com.foodenak.foodenak.rest

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import okhttp3.HttpUrl
import java.util.ArrayList

/**
 * Created by kukuh on 16/02/05.
 */
class ServiceEndPoints(private val context: Context) {

  fun url(): HttpUrl {
    if (devHttpUrl == null) {
      val url = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_END_POINT,
          devEndPoints[0])
      devHttpUrl = HttpUrl.parse(url + SERVICE)
    }
    return devHttpUrl!!
  }

  companion object {

    private val TAG = "ServiceEndPoints"
    private val END_POINT = "https://www.foodenak.com/"
    private val SERVICE = "api/"
    private val PREF_END_POINT = "com.foodenak.foodenak.utilities.ServiceEndPoints.PREF_END_POINT"
    val devEndPoints: MutableList<String> = ArrayList()
    private var devHttpUrl: HttpUrl? = null

    init {
      devEndPoints.add("http://dev.foodenak.com/")
      devEndPoints.add("http://192.168.1.163/FoodEnak-Web/")
      devEndPoints.add(END_POINT)
      devEndPoints.add("http://192.168.1.105/foodenak/")
    }

    fun setDevEndPoint(context: Context, devUrl: String) {
      devHttpUrl = HttpUrl.parse(devUrl + SERVICE)
      if (!devEndPoints.contains(devUrl)) devEndPoints.add(devUrl)
      PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_END_POINT,
          devUrl).commit()
      Log.i(TAG, "dev end point changed to " + devUrl)
    }

    fun getDevEndPoint(context: Context): String {
      if (devHttpUrl == null) {
        val url = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_END_POINT,
            devEndPoints[0])
        devHttpUrl = HttpUrl.parse(url + SERVICE)
      }
      return devHttpUrl!!.toString()
    }
  }
}
