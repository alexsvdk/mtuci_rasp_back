package ru.mtuci.core

import ru.mtuci.models.BaseDocument
import kotlin.reflect.KClass

interface RepositoryFactory {

    fun <T : BaseDocument> createRepository(
        clazz: KClass<T>
    ): BaseRepository<T>

}