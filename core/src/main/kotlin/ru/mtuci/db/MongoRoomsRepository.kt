package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import ru.mtuci.core.RoomsRepository
import ru.mtuci.models.Room

class MongoRoomsRepository(database: MongoDatabase) :
    MongoBaseRepository<Room>(database, Room::class.java),
    RoomsRepository {

    override fun findByNumber(number: String): Room? {
        return collection.findOne { Room::number eq number }
    }

}