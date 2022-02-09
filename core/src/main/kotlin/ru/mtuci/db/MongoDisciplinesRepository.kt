package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.path
import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.models.Discipline
import ru.mtuci.models.DisciplinesPagination

class MongoDisciplinesRepository(database: MongoDatabase) :
    MongoBaseRepository<Discipline>(database, Discipline::class.java), DisciplinesRepository {

    override fun findByName(name: String): Discipline? {
        return collection.findOne { Discipline::name eq name }
    }

    override fun softSearch(search: String?, offset: Int, limit: Int): DisciplinesPagination {
        val filters = mutableListOf<Bson>()
        search?.let {
            filters.add(
                Filters.regex(
                    Discipline::name.path(),
                    it,
                    "i"
                )
            )
        }
        val findRes = if (filters.isNotEmpty()) collection.find(and(filters)) else collection.find()
        val total = findRes.count()
        return DisciplinesPagination(total, findRes.skip(offset).limit(limit).toList())
    }


}