package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.*
import ru.mtuci.core.Repository
import ru.mtuci.core.RepositoryFactory
import ru.mtuci.models.BaseDocument
import kotlin.reflect.KClass

class MongoRepositoryFactory(
    private val database: MongoDatabase,
) : RepositoryFactory {

    override fun <T : BaseDocument> createRepository(clazz: KClass<T>): Repository<T> {
        return MongoRepository(database, clazz.java)
    }

}