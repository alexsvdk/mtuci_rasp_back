package ru.mtuci.core

import ru.mtuci.models.Discipline
import ru.mtuci.models.DisciplinesPagination

interface DisciplinesRepository : BaseRepository<Discipline> {

    fun findByName(
        name: String,
    ): Discipline?

    fun softSearch(
        search: String? = null,
        offset: Int = 0,
        limit: Int = 50,
    ): DisciplinesPagination

}