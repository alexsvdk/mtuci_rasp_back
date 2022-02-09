package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.path
import ru.mtuci.core.RoomsRepository
import ru.mtuci.models.Room
import ru.mtuci.models.RoomsPagination

class MongoRoomsRepository(database: MongoDatabase) :
    MongoBaseRepository<Room>(database, Room::class.java),
    RoomsRepository {

    override fun findByNumber(number: String): Room? {
        return collection.findOne { Room::number eq number }
    }

    override fun softSearch(search: String?, offset: Int, limit: Int): RoomsPagination {
        val filters = mutableListOf<Bson>()

        search?.let {
            filters.add(
                Filters.regex(
                    Room::number.path(),
                    it,
                    "i"
                )
            )
        }

        val findRes = if (filters.isNotEmpty()) collection.find(and(filters)) else collection.find()
        val total = findRes.count()
        return RoomsPagination(total, findRes.skip(offset).limit(limit).toList())
    }

}