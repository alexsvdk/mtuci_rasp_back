package ru.mtuci.parser

import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.core.RoomsRepository
import ru.mtuci.core.TeachersRepository
import ru.mtuci.di.koin
import ru.mtuci.models.*

private val innerRex = "\\([^]]+\\)".toRegex()
private val ends = listOf("по", "до")
private val starts = listOf("с")
private val roomRex1 = "ауд[.] +[A-Z,0-9,a-z,а-я, А-Я, -]{2,7} *".toRegex()
private val roomRex2 = "А-[0-9]{2,5}".toRegex()

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
        it.roomId = getRooms()?.id
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
        var newName = this.name?.replace(innerRex, "") ?: return null
        newName = newName.replace("\n", " ")
        newName = newName.replace("/", " ")
            .replace(roomRex1, "")
            .replace(roomRex2, "")
            .trim()

        val repo = koin.get<DisciplinesRepository>()

        return repo.findByName(newName) ?: Discipline().let {
            it.name = newName
            repo.save(it)
        }
    }

    private fun getRooms(): Room? {
        val name = this.name?.lowercase() ?: return null
        var room = roomRex1.findAll(name).lastOrNull()?.value?.replace("ауд.", "")?.trim()
        if (room == null) room = roomRex2.findAll(name).lastOrNull()?.value?.trim()
        if (room == null) {
            return null
        }

        val repo = koin.get<RoomsRepository>()
        return repo.findByNumber(room) ?: Room().let {
            it.number = room
            it.floor = room.filter { it.isDigit() }.toIntOrNull()?.let { if (it > 100) (it / 100) else 1 }
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