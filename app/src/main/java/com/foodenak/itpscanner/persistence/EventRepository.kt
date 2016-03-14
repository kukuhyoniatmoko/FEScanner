package com.foodenak.itpscanner.persistence

import android.content.Context
import android.preference.PreferenceManager
import com.foodenak.itpscanner.entities.Event
import com.foodenak.itpscanner.entities.EventImage
import com.foodenak.itpscanner.persistence.dao.EventEntityDao
import com.foodenak.itpscanner.persistence.dao.EventImageEntityDao
import com.foodenak.itpscanner.persistence.dao.HistoryEntityDao
import rx.Observable
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by ITP on 10/8/2015.
 */
@Singleton
class EventRepository @Inject constructor(context: Context, val eventDao: EventEntityDao, val eventImageDao: EventImageEntityDao, val historyDao: HistoryEntityDao) {

    val toEventConverter = EventEntityToEventConverter()

    val toEntityConverter = EventToEventEntityConverter()

    val toEventImageConverter = EventImageEntityToEventImageConverter()

    val toImageEntityConverter = EventImageToEventImageEntityConverter()

    val eventsQuery = eventDao.queryBuilder()
            .build()

    val eventQuery = eventDao.queryBuilder()
            .where(EventEntityDao.Properties.ServerId.eq(null))
            .build()

    val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    val deleteImagesEntityQuery = eventImageDao.queryBuilder()
            .where(EventImageEntityDao.Properties.EventId.eq(null))
            .buildDelete()

    fun setLastHistory(time: Long) {
        preferences.edit().putLong(LAST_HISTORY_TIME, time).commit()
    }

    fun getLastHistory(): Long {
        return preferences.getLong(LAST_HISTORY_TIME, INVALID_HISTORY_TIME)
    }

    fun getEvents(): Observable<List<Event>> {
        return Observable.defer {
            val entities = eventsQuery.forCurrentThread().list()
            if (entities.isEmpty()) {
                return@defer Observable.empty<List<Event>>()
            }
            val events: List<Event> = ArrayList()
            for (entity in entities) {
                val event = toEventConverter.convert(entity)
                if (event != null) {
                    val imageEntities = entity.eventImageEntityList
                    val images: List<EventImage> = ArrayList()
                    for (imageEntity in imageEntities) {
                        images.plus(toEventImageConverter.convert(imageEntity))
                    }
                    event.eventImages = images
                    events.plus(event)
                }
            }
            return@defer Observable.just(events)
        }
    }

    fun getEvent(id: Long): Observable<Event> {
        return Observable.defer {
            val query = eventQuery.forCurrentThread()
            query.setParameter(0, id)
            val entity = query.unique()
            val event = toEventConverter.convert(entity) ?: return@defer Observable.empty<Event>()
            val imageEntities = entity.eventImageEntityList
            val images: List<EventImage> = ArrayList()
            for (imageEntity in imageEntities) {
                images.plus(toEventImageConverter.convert(imageEntity))
            }
            event.eventImages = images
            return@defer Observable.just(event)
        }
    }

    fun deleteAllEvent() {
        val db = eventDao.database
        db.beginTransaction()
        try {
            val entities = eventsQuery.forCurrentThread().list()
            entities.forEach { entity ->
                val deleteImagesQuery = deleteImagesEntityQuery.forCurrentThread()
                deleteImagesQuery.setParameter(0, entity.id)
                deleteImagesQuery.executeDeleteWithoutDetachingEntities()
            }
            eventDao.deleteAll()
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun saveEvents(events: List<Event>) {
        val db = eventDao.database
        db.beginTransaction()
        try {
            events.forEach { event -> saveEventInternal(event) }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun saveEvent(event: Event): Long {
        val db = eventDao.database
        db.beginTransaction()
        try {
            val id = saveEventInternal(event)
            db.setTransactionSuccessful()
            return id
        } finally {
            db.endTransaction()
        }
    }

    private fun saveEventInternal(event: Event): Long {
        val query = eventQuery.forCurrentThread()
        query.setParameter(0, event.id)
        val entity = query.unique()
        val newEntity = toEntityConverter.convert(event)
        if (entity != null) {
            newEntity?.id = entity.id
            eventDao.update(newEntity)
            val deleteImagesQuery = deleteImagesEntityQuery.forCurrentThread()
            deleteImagesQuery.setParameter(0, entity.id)
            deleteImagesQuery.executeDeleteWithoutDetachingEntities()
        } else {
            eventDao.insert(newEntity)
        }

        event.eventImages?.forEach { image ->
            val imageEntity = toImageEntityConverter.convert(image)
            imageEntity?.eventId = newEntity!!.id!!
            eventImageDao.insert(imageEntity)
        }
        return newEntity!!.id!!
    }

    companion object {

        internal val LAST_HISTORY_TIME = "LAST_HISTORY_TIME"

        val INVALID_HISTORY_TIME = -1.toLong()
    }
}