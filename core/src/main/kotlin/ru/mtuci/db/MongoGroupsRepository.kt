package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.path
import ru.mtuci.core.GroupsRepository
import ru.mtuci.models.Group
import ru.mtuci.models.GroupsPagination

class MongoGroupsRepository(database: MongoDatabase) : MongoBaseRepository<Group>(database, Group::class.java),
    GroupsRepository {

    override fun findByName(name: String): Group? {
        return collection.findOne { Group::name eq name }
    }

    override fun softSearch(search: String?, directionId: String?, offset: Int, limit: Int): GroupsPagination {
        val filters = mutableListOf<Bson>()

        search?.let {
            filters.add(
                Filters.regex(
                    Group::name.path(),
                    it,
                    "i"
                )
            )
        }

        directionId?.let {
            filters.add(Group::directionId eq it)
        }

        val findRes = if (filters.isNotEmpty()) collection.find(and(filters)) else collection.find()
        val total = findRes.count()
        return GroupsPagination(total, findRes.skip(offset).limit(limit).toList())
    }

}