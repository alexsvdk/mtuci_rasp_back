package ru.mtuci.core

import ru.mtuci.models.Teacher

interface TeachersRepository : BaseRepository<Teacher> {

    fun findByLastNameAndInitials(
        lastName: String,
        firstI: String?,
        fathersI: String?,
    ): Teacher?

}