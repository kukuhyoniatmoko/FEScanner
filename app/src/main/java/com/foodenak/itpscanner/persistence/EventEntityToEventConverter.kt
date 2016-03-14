package com.foodenak.itpscanner.persistence

import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.persistence.db.EventEntity

/**
 * Created by ITP on 10/8/2015.
 */
class EventEntityToEventConverter : Converter<EventEntity?, Event?> {
    override fun convert(source: EventEntity?): Event? {
        if (source == null) {
            return null
        }
        val result = Event()
        result.id = source.serverId
        result.name = source.name
        result.description = source.description
        result.slug = source.slug
        result.isShown = source.isShown
        result.views = if (source.views != null) source.views else 0
        result.startDate = source.startDate
        result.endDate = source.endDate
        return result
    }
}