package com.foodenak.itpscanner.commons.android

import android.annotation.TargetApi
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.os.PowerManager
import com.foodenak.itpscanner.commons.NetworkChecker

/**
 * Created by ITP on 5/23/16.
 */
class AndroidNetworkChecker(private val context: Context) : NetworkChecker {
  override fun isOnline(): Boolean {
    if (isDozing()) return false
    val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkInfo = manager.activeNetworkInfo
    return networkInfo != null && networkInfo.isConnectedOrConnecting
  }

  @TargetApi(Build.VERSION_CODES.M) private fun isDozing(): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
      return powerManager.isDeviceIdleMode && !powerManager.isIgnoringBatteryOptimizations(
          context.packageName)
    } else {
      return false
    }
  }
}