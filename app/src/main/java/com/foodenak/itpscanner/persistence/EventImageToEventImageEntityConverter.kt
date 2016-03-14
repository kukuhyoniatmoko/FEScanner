package com.foodenak.itpscanner.persistence

import com.foodenak.itpscanner.entities.EventImage
import com.foodenak.itpscanner.persistence.db.EventImageEntity

/**
 * Created by ITP on 10/8/2015.
 */
class EventImageToEventImageEntityConverter : Converter<EventImage?, EventImageEntity?> {
    override fun convert(source: EventImage?): EventImageEntity? {
        if (source == null) {
            return null
        }
        val result = EventImageEntity()
        result.imageUrl = source.thumbUrl?.original
        return result
    }
}