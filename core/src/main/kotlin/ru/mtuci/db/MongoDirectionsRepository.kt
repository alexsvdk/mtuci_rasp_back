package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import ru.mtuci.core.DirectionsRepository
import ru.mtuci.models.Direction

class MongoDirectionsRepository(database: MongoDatabase) :
    MongoBaseRepository<Direction>(database, Direction::class.java),
    DirectionsRepository {

    override fun findByCode(code: String): Direction? {
        return collection.findOne { Direction::code eq code }
    }

}