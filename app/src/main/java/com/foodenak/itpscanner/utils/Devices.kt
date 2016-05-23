package com.foodenak.itpscanner.utils

import android.os.Build

/**
 * Created by ITP on 3/13/2015.
 */
object Devices {

    private var sDeviceName: String? = null

    val deviceName: String
        get() {
            if (sDeviceName == null) {
                sDeviceName = friendlyDeviceName
            }
            return sDeviceName!!
        }

    /**
     * Returns the consumer friendly device name
     */
    private val friendlyDeviceName: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            if (model.startsWith(manufacturer, true)) {
                return model.capitalizeEachWord()
            }
            if (manufacturer.equals("HTC", true)) { // make sure "HTC" is fully capitalized.
                return "HTC $model"
            }
            return "${manufacturer.capitalizeEachWord()} $model"
        }
}
