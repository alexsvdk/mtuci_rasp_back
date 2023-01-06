package ru.mtuci.ics_backend.calculator


import io.ktor.server.plugins.*
import net.fortuna.ical4j.model.Calendar
import net.fortuna.ical4j.model.ParameterList
import net.fortuna.ical4j.model.TimeZoneRegistry
import net.fortuna.ical4j.model.TimeZoneRegistryFactory
import net.fortuna.ical4j.model.component.VEvent
import net.fortuna.ical4j.model.parameter.Cn
import net.fortuna.ical4j.model.parameter.PartStat
import net.fortuna.ical4j.model.property.*
import ru.mtuci.Config
import ru.mtuci.calculators.DayLessonsCalculator
import ru.mtuci.calculators.FilterHashCalculator
import ru.mtuci.core.CalendarDataRepository
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.models.LessonType
import ru.mtuci.models.Room
import ru.mtuci.models.RoomLocation
import save
import java.io.File
import java.text.SimpleDateFormat
import java.time.temporal.Temporal
import java.util.*


class IcsCalculator(
    val calendarRepo: CalendarDataRepository,
    val lessonsRepository: RegularLessonsRepository,
    val filterHashCalculator: FilterHashCalculator,
) {

    private val icsDir = File(Config.APP_BASE_PATH, "ics")
    private val untilFormatter = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'")
    private val tzId = TimeZone.getTimeZone("Europe/Moscow")
    private val hourMs = 60 * 60 * 1000

    private val tz by lazy {
        val registry: TimeZoneRegistry = TimeZoneRegistryFactory.getInstance().createRegistry()
        registry.getTimeZone(tzId.id).vTimeZone
    }

    init {
        icsDir.mkdirs()
        if (!icsDir.exists()) {
            icsDir.mkdir()
        }
    }


    fun createIcsFile(id: String): File {
        val calendarData = calendarRepo.get(id) ?: throw NotFoundException()
        val lessons = lessonsRepository.findRegularLessons(calendarData.searchFilter).data

        val calendar = Calendar()
        calendar.add(ProdId("-//MTUCI//iCal4j 1.0//RU"))
        calendar.add(Version(ParameterList(), "2.0"))
        calendar.add(CalScale(CalScale.VALUE_GREGORIAN))
        calendar.add(tz)

        for (lesson in lessons) {
            val firstLesson = DayLessonsCalculator.calculateFirstLesson(lesson) ?: continue
            val lastLesson = DayLessonsCalculator.calculateLastLesson(lesson) ?: continue


            // create base event
            val discipline = lesson.getDiscipline()
            val eventName = "${mapLessonTypeToEmoji(lesson.lessonType)} ${discipline?.name}"
            val startTemporal =
                Date(firstLesson.startTime - DayLessonsCalculator.timeZone.rawOffset - hourMs).toInstant()
                    .atZone(tzId.toZoneId())
            val endTemporal = Date(firstLesson.endTime - DayLessonsCalculator.timeZone.rawOffset - hourMs).toInstant()
                .atZone(tzId.toZoneId())
            val event = VEvent(startTemporal, endTemporal, eventName)

            // set color
            event.add(Color(ParameterList(), mapLessonTypeToColor(lesson.lessonType)))

            // set time zone
            event.add(TzId(tzId.id))

            //set event id
            event.add(Uid(lesson.id))

            //set repeat every two weeks until lastLesson
            val rrule = RRule<Temporal>(
                "FREQ=WEEKLY;INTERVAL=2;UNTIL=${untilFormatter.format(Date(lastLesson.startTime))}"
            )
            event.add(rrule)

            // set event location
            lesson.getRoom()?.let { room ->
                event.add(Location(room.number))
                mapRoomToGeo(room)?.let { event.add(it) }
            }

            // set event participants
            lesson.getTeacher()?.let { teacher ->
                val attendee = Organizer("mailto:${teacher.id?.last()}@mtuci.ru")
                attendee.withParameter(Cn(teacher.shortName)).withParameter(PartStat.ACCEPTED)
                event.add(attendee)
            }
            lesson.getGroups().forEach { group ->
                val attendee = Attendee("mailto:${group.id?.last()}@mtuci.ru")
                attendee.withParameter(Cn(group.name)).withParameter(PartStat.ACCEPTED)
                event.add(attendee)
            }

            // add event to calendar
            calendar.add(event)
        }

        calendarData.savedFiltersRevisionsHash = filterHashCalculator.getFilterHash(calendarData.searchFilter)
        calendarData.calculatorVersion = Config.CALCULATOR_VERSION
        calendarData.save()

        val file = File(icsDir, "$id.ics")
        file.createNewFile()
        file.writeText(calendar.toString())
        return file
    }

    fun removeIcsFile(id: String) {
        val file = File(icsDir, "$id.ics")
        file.delete()
    }

    private fun mapLessonTypeToEmoji(type: LessonType): String {
        return when (type) {
            LessonType.LECTURE -> "📚"
            LessonType.PRACTICE -> "🔧"
            LessonType.LABORATORY -> "🧪"
            LessonType.EXAM -> "📝"
            LessonType.UNKNOWN -> "📝"
            LessonType.SPORT -> "🏃"
        }
    }

    private fun mapLessonTypeToColor(type: LessonType): String {
        return when (type) {
            LessonType.LECTURE -> "#FF0000"
            LessonType.PRACTICE -> "#00FF00"
            LessonType.LABORATORY -> "#0000FF"
            LessonType.EXAM -> "#FF00FF"
            LessonType.UNKNOWN -> "#FF00FF"
            LessonType.SPORT -> "#00FFFF"
        }
    }

    private fun mapRoomToDesc(room: Room): String? {
        return when (room.location) {
            RoomLocation.MOTOR -> "Авиамоторная"
            RoomLocation.NAROD -> "Улица Народного Ополчения"
            else -> null
        }
    }

    private fun mapRoomToGeo(room: Room): Geo? {
        return when (room.location) {
            RoomLocation.MOTOR -> Geo(55.755159.toBigDecimal(), 37.712420.toBigDecimal())
            RoomLocation.NAROD -> Geo(55.784192.toBigDecimal(), 37.482507.toBigDecimal())
            else -> null
        }
    }

}