package ru.mtuci.parser

import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.core.TeachersRepository
import ru.mtuci.di.koin
import ru.mtuci.models.*

private val innerRex = "\\([^]]+\\)".toRegex()
private val ends = listOf("по", "до")
private val starts = listOf("с")

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


    var group: Group? = null


    fun buildLesson(): RegularLesson = RegularLesson().also {
        it.lessonType = getLessonType()
        it.teacherId = getTeacher()?.id
        it.disciplineId = getDiscipline()?.id
        it.isDistant = dist
        it.tweekDay = day
        it.lessonNum = num
        it.dateFrom = getStartTime()
        it.dateTo = getEndTime()
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
        val initials = name.substringAfter(" ").replace(" ", "")
        val firstI = initials.getOrNull(0)?.toString()
        val fathersI = initials.getOrNull(2)?.toString()

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
        val name = this.name?.replace(innerRex, "") ?: return null
        val repo = koin.get<DisciplinesRepository>()

        return repo.findByLastName(name) ?: Discipline().let {
            it.name = name
            repo.save(it)
        }
    }

    private fun getEndTime(): Long? {
        val matches = name?.let { innerRex.findAll(it) }?.map { it.value.lowercase() }?.toList() ?: return null
        for (match in matches) {
            if (ends.any(match::contains)) {
                val dates = dateRex.findAll(match).map { it.value }.toList()
                dates.lastOrNull()?.let {
                    return dateFormat.parse(it).time
                }
            }
        }
        return null
    }

    private fun getStartTime(): Long? {
        val matches = name?.let { innerRex.findAll(it) }?.map { it.value.lowercase() }?.toList() ?: return null
        for (match in matches) {
            if (starts.any(match::contains)) {
                val fdates = dateFRex.findAll(match).map { it.value }.toList()
                val dates = dateRex.findAll(match).map { it.value }.toList()
                if (fdates.isNotEmpty() && dates.isNotEmpty()) {
                    val date = fdates.first().substring(0, 5) + "." + dates.first().substringAfterLast(".")
                    return dateFormat.parse(date).time
                }
                dates.firstOrNull()?.let {
                    return dateFormat.parse(it).time
                }
            }
        }
        return null
    }

}