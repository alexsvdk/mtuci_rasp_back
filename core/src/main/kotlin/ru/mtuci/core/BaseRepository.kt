package ru.mtuci.core

import ru.mtuci.models.BaseDocument

interface BaseRepository<T : BaseDocument> {

    fun save(doc: T): T

    fun get(id: String): T?

    fun getMany(ids: List<String>) = ids.map { get(it) }.filterNotNull().toList();

    fun remove(id: String): Boolean

    fun removeAll()

}