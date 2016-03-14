package com.foodenak.itpscanner.persistence

import com.foodenak.itpscanner.entities.EventImage
import com.foodenak.itpscanner.entities.ThumbUrl
import com.foodenak.itpscanner.persistence.db.EventImageEntity

/**
 * Created by ITP on 10/8/2015.
 */
class EventImageEntityToEventImageConverter : Converter<EventImageEntity?, EventImage?> {
    override fun convert(source: EventImageEntity?): EventImage? {
        if (source == null) {
            return null
        }
        val result = EventImage()
        val thumbUrl = source.imageUrl ?: return result
        result.thumbUrl = ThumbUrl(thumbUrl)
        return result
    }
}