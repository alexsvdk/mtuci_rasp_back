package ru.mtuci.utils

import ru.mtuci.models.DayLesson
import ru.mtuci.models.Group
import ru.mtuci.models.RegularLesson
import java.util.*

object DayLessonsCalculator {

    private const val minuteMs: Long = 60 * 1000
    private const val hourMs: Long = 60 * minuteMs
    private const val dayMs: Long = 24 * hourMs

    private val pairsDur = listOf(
        Pair(9 * hourMs + 30 * minuteMs, 11 * hourMs + 5 * minuteMs),
        Pair(11 * hourMs + 20 * minuteMs, 12 * hourMs + 50 * minuteMs),
        Pair(13 * hourMs + 10 * minuteMs, 14 * hourMs + 45 * minuteMs),
        Pair(15 * hourMs + 25 * minuteMs, 17 * hourMs),
        Pair(17 * hourMs + 15 * minuteMs, 18 * hourMs + 50 * minuteMs),
    )

    fun calculateDayLessons(
        regularLesson: List<RegularLesson>, dateFrom: Long, dateTo: Long, group: Group?
    ): List<DayLesson> {
        val res = mutableListOf<DayLesson>()
        val cal = GregorianCalendar(TimeZone.getTimeZone("GMT+3:00"))
        cal.firstDayOfWeek = Calendar.MONDAY

        for (dateTime in dateFrom..dateTo step dayMs) {
            for (lesson in regularLesson) {
                val id = lesson.id ?: continue
                if (dateTime < (lesson.dateFrom ?: 0)) continue
                if (dateTime > (lesson.dateTo ?: Long.MAX_VALUE)) continue

                cal.time = lesson.dateFrom?.let { Date(it) } ?: continue

                //Хак чтобы неделя начиланась с понедельника
                while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) cal.add(Calendar.DATE, 1)

                val tweekDay = (((dateTime - cal.time.time) / dayMs) % 14).toInt()
                if (tweekDay == lesson.tweekDay) {
                    val dur = lesson.lessonNum?.let { pairsDur[it - 1] } ?: continue
                    res.add(
                        DayLesson(
                            cal.time.time + dur.first,
                            cal.time.time + dur.second,
                            id,
                        )
                    )
                }
            }
        }

        res.sortBy { it.startTime }
        return res
    }

}