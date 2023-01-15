package ru.mtuci.parser.rasp.parsers

import ru.mtuci.calculators.DayLessonsCalculator
import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.core.RoomsRepository
import ru.mtuci.core.TeachersRepository
import ru.mtuci.di.koin
import ru.mtuci.models.*
import ru.mtuci.models.lessons.BaseLesson
import ru.mtuci.models.lessons.RegularLesson
import ru.mtuci.parser.rasp.BuildLessonResult
import ru.mtuci.parser.rasp.RaspParserConstants
import ru.mtuci.parser.rasp.RaspParserConstants.dateFRex
import ru.mtuci.parser.rasp.RaspParserConstants.dateFormat
import ru.mtuci.parser.rasp.RaspParserConstants.datePartRex
import ru.mtuci.parser.rasp.RaspParserConstants.dateRex
import ru.mtuci.parser.rasp.RaspParserConstants.ends
import ru.mtuci.parser.rasp.RaspParserConstants.innerRex
import ru.mtuci.parser.rasp.RaspParserConstants.starts
import ru.mtuci.parser.rasp.RaspParserConstants.yearRex
import java.util.*


class RawLesson(
    var day: Int? = null
) {

    /// raw data
    var name: String? = null
    var teacher: String? = null
    var type: String? = null
    var num: Int? = null
    var room: String? = null
    var group: Group? = null

    var date: String? = null
    var time: String? = null
    var studyYear: String? = null
    var sheetTitle: String? = null


    fun buildRegularLesson() = BuildLessonResult<RegularLesson>().apply {
        teacher =  getTeacher()
        discipline = getDiscipline()
        room = getRooms()
        lesson = RegularLesson().also {
            it.lessonType = getLessonType()
            it.teacherId = teacher?.id
            it.disciplineId = discipline?.id
            it.tweekDay = day
            it.lessonNum = num
            it.dateFrom = getStartTime()
            it.dateTo = getEndTime()
            it.roomId = room?.id
            it.tags = getTags()
        }
    }

    fun buildExamLesson() = BuildLessonResult<BaseLesson>().apply {
        teacher =  getTeacher()
        discipline = getDiscipline()
        room = getRooms()
        lesson = BaseLesson().also {
            it.lessonType = getLessonType()
            it.teacherId = teacher?.id
            it.disciplineId = discipline?.id
            it.lessonNum = num
            it.roomId = room?.id
            it.dateFrom = getStartTime()
            it.dateTo = getEndTime()
        }
    }


    private fun getLessonType() = when (type?.lowercase()?.replace(".", "")) {
        "л" -> LessonType.LECTURE
        "пр" -> LessonType.PRACTICE
        "лаб" -> LessonType.LABORATORY
        "фв" -> LessonType.SPORT
        "конс" -> LessonType.CONSULTATION
        "экзамен" -> LessonType.EXAM
        else -> LessonType.UNKNOWN
    }

    private fun getTeacher() = teacher?.let { name ->
        val repo = koin.get<TeachersRepository>()

        val match = RaspParserConstants.fioRex.find(name)

        if (match != null) {
            val lastName = match.groups[1]!!.value
            val firstI = match.groups[2]!!.value
            val fathersI = match.groups[3]!!.value
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
        getExamDate()?.let { examDate ->
            val rawEndTime = time?.substringAfter("-")?.trim()
            examDate.set(Calendar.HOUR_OF_DAY, rawEndTime?.substringBefore(".")?.toIntOrNull() ?: 0)
            examDate.set(Calendar.MINUTE, rawEndTime?.substringAfter(".")?.toIntOrNull() ?: 0)
            return examDate.timeInMillis
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
        getExamDate()?.let { examDate ->
            val rawStartTime = time?.substringBefore("-")?.trim()
            examDate.set(Calendar.HOUR_OF_DAY, rawStartTime?.substringBefore(".")?.toIntOrNull() ?: 0)
            examDate.set(Calendar.MINUTE, rawStartTime?.substringAfter(".")?.toIntOrNull() ?: 0)
            return examDate.timeInMillis
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

    private fun getExamDate(): Calendar? {
        val years = yearRex.findAll(studyYear ?: "").mapNotNull { it.value.toIntOrNull() }.toList()
        val startYear = years.firstOrNull() ?: return null
        val endYear = years.lastOrNull() ?: return null
        val datePart = datePartRex.find(date ?: "")?.value ?: return null
        val month = datePart.substring(3, 5).toIntOrNull() ?: return null
        val year = if (month<9) endYear else startYear
        val cal = GregorianCalendar(DayLessonsCalculator.timeZone)
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month-1)
        cal.set(Calendar.DAY_OF_MONTH, datePart.substring(0, 2).toIntOrNull() ?: return null)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }

}