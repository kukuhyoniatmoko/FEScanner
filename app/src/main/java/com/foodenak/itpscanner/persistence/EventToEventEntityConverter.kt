package com.foodenak.itpscanner.persistence

import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.persistence.db.EventEntity

/**
 * Created by ITP on 10/8/2015.
 */
class EventToEventEntityConverter : Converter<Event?, EventEntity?> {
    override fun convert(source: Event?): EventEntity? {
        if (source == null) {
            return null
        }
        val result = EventEntity()
        result.serverId = source.id
        result.name = source.name
        result.description = source.description
        result.slug = source.slug
        result.isShown = source.isShown
        result.views = source.views
        result.startDate = source.startDate
        result.endDate = source.endDate
        return result
    }
}