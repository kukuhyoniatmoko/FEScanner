package com.foodenak.itpscanner.entities

import java.io.Serializable
import java.util.Collections
import java.util.Date

/**
 * Created by ITP on 10/6/2015.
 */
data class Event(

    var id: Long? = null,

    var name: String? = null,

    var description: String? = null,

    var slug: String? = null,

    var startDate: Date? = null,

    var endDate: Date? = null,

    var isShown: Boolean = false,

    var views: Int = 0,

    var eventImages: List<EventImage>? = Collections.emptyList()) : Serializable {
}