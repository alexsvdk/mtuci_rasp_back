package ru.mtuci.parser

import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.core.TeachersRepository
import ru.mtuci.di.koin
import ru.mtuci.models.Discipline
import ru.mtuci.models.LessonType
import ru.mtuci.models.RegularLesson
import ru.mtuci.models.Teacher

class RawRepeatedLesson(
    var day: Int
) {

    /// raw data
    var name: String? = null
    var teacher: String? = null
    var type: String? = null
    var dist: Boolean = false
    var num: Int? = null
    var sec: Boolean = false


    fun buildLesson(): RegularLesson = RegularLesson().also {
        it.lessonType = getLessonType()
        it.teacherId = getTeacher()?.id
        it.disciplineId = getDiscipline()?.id
        it.isDistant = dist
        it.tweekDay = day
        it.lessonNum = num
    }

    private fun getLessonType() = when (type?.lowercase()) {
        "лек" -> LessonType.LECTURE
        "пр" -> LessonType.PRACTICE
        "лаб" -> LessonType.LABORATORY
        else -> LessonType.UNKNOWN
    }

    private fun getTeacher() = teacher?.let { name ->
        val repo = koin.get<TeachersRepository>()

        val lastName = name.substringBefore(" ")
        val initials = name.substringAfter(" ")
        val firstI = initials.getOrNull(0)?.toString()
        val fathersI = initials.getOrNull(3)?.toString()

        repo.findByLastNameAndInitials(
            lastName, firstI, fathersI
        ) ?: Teacher().let {
            it.lastName = lastName
            it.fathersNameI = fathersI
            it.firstNameI = firstI

            repo.save(it)
        }
    }

    private fun getDiscipline(): Discipline? {
        val name = this.name?.replace("/\\([^]]+\\)/gm".toRegex(), "") ?: return null
        val repo = koin.get<DisciplinesRepository>()

        return repo.findByLastName(name) ?: Discipline().let {
            it.name = name
            repo.save(it)
        }
    }

}