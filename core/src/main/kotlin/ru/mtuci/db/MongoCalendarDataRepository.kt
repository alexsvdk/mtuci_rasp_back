package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.bson.conversions.Bson
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.`in`
import org.litote.kmongo.or
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
        if (teachers.isNotEmpty()){
            query.add(SearchFilter::teacherId `in` teachers)
        }
        if (disciplines.isNotEmpty()){
            query.add(SearchFilter::disciplineId `in` disciplines)
        }
        if (rooms.isNotEmpty()){
            query.add(SearchFilter::roomId `in` rooms)
        }
        if (groups.isNotEmpty()){
            query.add(SearchFilter::groupId `in` groups)
        }

        return collection.find(or(query)).toList()
    }

}