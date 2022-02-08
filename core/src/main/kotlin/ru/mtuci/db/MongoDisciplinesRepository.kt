package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.models.Discipline

class MongoDisciplinesRepository(database: MongoDatabase) :
    MongoBaseRepository<Discipline>(database, Discipline::class.java), DisciplinesRepository {

    override fun findByLastName(name: String): Discipline? {
        return collection.findOne { Discipline::name eq name }
    }


}