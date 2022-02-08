package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import ru.mtuci.core.GroupsRepository
import ru.mtuci.models.Group

class MongoGroupsRepository(database: MongoDatabase) : MongoBaseRepository<Group>(database, Group::class.java),
    GroupsRepository {

    override fun findByName(name: String): Group? {
        return collection.findOne { Group::name eq name }
    }

}