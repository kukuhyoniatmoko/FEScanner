package com.foodenak.itpscanner.entities

import android.text.TextUtils
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ITP on 10/8/2015.
 */
data class HistoryParameter(
        var eventId: Long,
        var page: Int = 1,
        var find: String? = null,
        var filterBefore: Date? = null,
        var filterAfter: Date? = null
) : Serializable {

    fun createMap(): Map<String, String> {
        val map = HashMap<String, String> ()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        if (!TextUtils.isEmpty(find)) {
            map.put("find", find!!)
        }
        if (filterBefore != null) {
            val before = dateFormat.format(filterBefore)
            map.put("filter_before", before)
        }
        if (filterAfter != null) {
            val after = dateFormat.format(filterAfter)
            map.put("filter_after", after)
        }
        if (page > 1) {
            map.put("page", page.toString());
        }
        return map
    }

    fun isEmpty(): Boolean {
        return TextUtils.isEmpty(find) && filterBefore == null && filterAfter == null && page <= 1
    }
}