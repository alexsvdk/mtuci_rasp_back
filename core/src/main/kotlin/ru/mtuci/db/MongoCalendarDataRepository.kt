package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*
import ru.mtuci.core.CalendarDataRepository
import ru.mtuci.models.CalendarData
import ru.mtuci.models.SearchFilter

class MongoCalendarDataRepository(database: MongoDatabase) :
    MongoBaseRepository<CalendarData>(database, CalendarData::class.java),
    CalendarDataRepository {
    override fun findBySearchFilter(searchFilter: SearchFilter): CalendarData? {
        return collection.findOne { CalendarData::searchFilter eq searchFilter }
    }

}