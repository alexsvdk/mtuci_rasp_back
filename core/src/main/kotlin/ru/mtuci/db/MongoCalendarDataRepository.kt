package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.bson.conversions.Bson
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

    override fun findByAnyOf(
        teachers: List<String>,
        disciplines: List<String>,
        rooms: List<String>,
        groups: List<String>
    ): List<CalendarData> {
        val query = mutableListOf<Bson>()
        if (teachers.isNotEmpty()) {
            query.add(CalendarData::searchFilter / SearchFilter::teacherId `in` teachers)
        }
        if (disciplines.isNotEmpty()) {
            query.add(CalendarData::searchFilter / SearchFilter::disciplineId `in` disciplines)
        }
        if (rooms.isNotEmpty()) {
            query.add(CalendarData::searchFilter / SearchFilter::roomId `in` rooms)
        }
        if (groups.isNotEmpty()) {
            query.add(CalendarData::searchFilter / SearchFilter::groupId `in` groups)
        }

        return collection.find(or(query)).toList()
    }

}