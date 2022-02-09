package ru.mtuci.core

import ru.mtuci.models.Teacher
import ru.mtuci.models.TeachersPagination

interface TeachersRepository : BaseRepository<Teacher> {

    fun findByLastNameAndInitials(
        lastName: String,
        firstI: String?,
        fathersI: String?,
    ): Teacher?

    fun softSearch(
        search: String? = null,
        offset: Int = 0,
        limit: Int = 50,
    ): TeachersPagination

}