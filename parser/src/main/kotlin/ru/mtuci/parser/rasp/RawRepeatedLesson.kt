package ru.mtuci.parser.rasp

import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.core.RoomsRepository
import ru.mtuci.core.TeachersRepository
import ru.mtuci.di.koin
import ru.mtuci.models.*
import ru.mtuci.parser.rasp.RaspParserConstants.dateFRex
import ru.mtuci.parser.rasp.RaspParserConstants.dateFormat
import ru.mtuci.parser.rasp.RaspParserConstants.dateRex
import ru.mtuci.parser.rasp.RaspParserConstants.ends
import ru.mtuci.parser.rasp.RaspParserConstants.innerRex
import ru.mtuci.parser.rasp.RaspParserConstants.starts


class RawRepeatedLesson(
    var day: Int
) {

    /// raw data
    var name: String? = null
    var teacher: String? = null
    var type: String? = null
    var num: Int? = null
    var room: String? = null

    var group: Group? = null


    fun buildLesson(): RegularLesson = RegularLesson().also {
        it.lessonType = getLessonType()
        it.teacherId = getTeacher()?.id
        it.disciplineId = getDiscipline()?.id
        it.tweekDay = day
        it.lessonNum = num
        it.dateFrom = getStartTime()
        it.dateTo = getEndTime()
        it.roomId = getRooms()?.id
        it.tags = getTags()
    }

    private fun getLessonType() = when (type?.lowercase()?.replace(".", "")) {
        "л" -> LessonType.LECTURE
        "пр" -> LessonType.PRACTICE
        "лаб" -> LessonType.LABORATORY
        "фв" -> LessonType.SPORT
        else -> LessonType.UNKNOWN
    }

    private fun getTeacher() = teacher?.let { name ->
        val repo = koin.get<TeachersRepository>()

        val match = RaspParserConstants.fioRex.find(name)

        if (match != null) {
            val lastName = match.groups[0]!!.value
            val firstI = match.groups[1]!!.value
            val fathersI = match.groups[2]!!.value
            repo.findByLastNameAndInitials(
                lastName, firstI, fathersI
            ) ?: Teacher().let {
                it.lastName = lastName
                it.fathersNameI = fathersI
                it.firstNameI = firstI
                repo.save(it)
            }
        } else null
    }

    private fun getDiscipline(): Discipline? {
        var newName = this.name ?: return null
        newName = newName.replace(innerRex, "")
            .replace("\n", " ")
            .replace("/", " ")

        val repo = koin.get<DisciplinesRepository>()

        return repo.findByName(newName) ?: Discipline().let {
            it.name = newName
            repo.save(it)
        }
    }

    private fun getRooms(): Room? {
        val room = this.room?.uppercase()?.trim()
        if (room?.isBlank() != false) {
            return null
        }

        val repo = koin.get<RoomsRepository>()
        return repo.findByNumber(room) ?: Room().let {
            it.number = room
            it.floor = room.filter { it.isDigit() }.toIntOrNull()?.let { if (it > 100) (it / 100) else 1 }
            it.location = when (room[0]) {
                'А' -> RoomLocation.MOTOR
                'A' -> RoomLocation.MOTOR
                'Н' -> RoomLocation.NAROD
                'N' -> RoomLocation.NAROD
                else -> null
            }
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

    private fun getTags(): MutableList<String> {
        if (name?.contains('(') != true) return mutableListOf()
        var nameIter = name!!
        val tags = mutableListOf<String>()

        while (nameIter.contains('(')) {
            tags.add(nameIter.substringAfter('(').substringBefore(')').trim())
            nameIter = nameIter.substringAfter(')')
        }
        return tags
    }

}