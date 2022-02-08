package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import ru.mtuci.core.BaseRepository
import ru.mtuci.core.RepositoryFactory
import ru.mtuci.models.BaseDocument
import kotlin.reflect.KClass

class MongoRepositoryFactory(
    private val database: MongoDatabase,
) : RepositoryFactory {

    override fun <T : BaseDocument> createRepository(clazz: KClass<T>): BaseRepository<T> {
        return MongoBaseRepository(database, clazz.java)
    }

}