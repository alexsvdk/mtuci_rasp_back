package ru.mtuci.core

import ru.mtuci.models.Discipline

interface DisciplinesRepository : BaseRepository<Discipline> {

    fun findByLastName(
        name: String,
    ): Discipline?

}