package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.deleteOneById
import org.litote.kmongo.findOneById
import ru.mtuci.core.BaseRepository
import ru.mtuci.models.common.BaseDocument

open class MongoBaseRepository<T : BaseDocument>(
    private val database: MongoDatabase,
    private val clazz: Class<T>
) : BaseRepository<T> {

    val collection = database.getCollection(clazz.simpleName, clazz)

    override fun save(doc: T): T {
        doc.id?.let(collection::deleteOneById)
        return doc.apply { collection.insertOne(doc) }
    }

    override fun get(id: String): T? {
        return collection.findOneById(id)
    }

    override fun getAll(): Iterable<T> {
        return collection.find()
    }

    override fun remove(id: String): Boolean {
        return collection.deleteOneById(id).deletedCount > 0
    }

    override fun removeAll() {
        collection.drop()
    }

}