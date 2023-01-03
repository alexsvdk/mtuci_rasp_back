package ru.mtuci.utils

import ru.mtuci.models.DayLesson
import ru.mtuci.models.RegularLesson
import java.util.*

object DayLessonsCalculator {

    private const val minuteMs: Long = 60 * 1000
    private const val hourMs: Long = 60 * minuteMs
     const val dayMs: Long = 24 * hourMs

    private val pairsDur = listOf(
        Pair(9 * hourMs + 30 * minuteMs, 11 * hourMs + 5 * minuteMs),
        Pair(11 * hourMs + 20 * minuteMs, 12 * hourMs + 50 * minuteMs),
        Pair(13 * hourMs + 10 * minuteMs, 14 * hourMs + 45 * minuteMs),
        Pair(15 * hourMs + 25 * minuteMs, 17 * hourMs),
        Pair(17 * hourMs + 15 * minuteMs, 18 * hourMs + 50 * minuteMs),
    )

    fun calculateDayLessons(
        regularLesson: List<RegularLesson>,
        dateFrom: Long,
        dateTo: Long?,
    ): List<DayLesson> {
        val dateTo = dateTo ?: Long.MAX_VALUE
        val res = mutableListOf<DayLesson>()
        val cal = GregorianCalendar(TimeZone.getTimeZone("GMT+3:00"))
        cal.firstDayOfWeek = Calendar.MONDAY

        for (lesson in regularLesson) {
            val id = lesson.id ?: continue
            if ((lesson.dateFrom ?: continue) > dateTo) continue
            if ((lesson.dateTo ?: continue) < dateFrom) continue

            cal.timeInMillis = lesson.dateFrom!!
            //Хак чтобы неделя начиланась с понедельника
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) cal.add(Calendar.DATE, 1)

            val dateFrom = maxOf(lesson.dateFrom!!, dateFrom)
            val dateTo = minOf(lesson.dateTo!!, dateTo)

            for (dateTime in dateFrom..dateTo step dayMs) {

                var tweekDay = (((dateTime - cal.time.time) / dayMs) % 14).toInt()
                if (tweekDay < -2) cal.add(Calendar.WEEK_OF_YEAR, -1)
                tweekDay = (((dateTime - cal.time.time) / dayMs) % 14).toInt()

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

    fun calculateDayLessonsForNextDays(
        regularLesson: List<RegularLesson>,
        dateFrom: Long,
        n: Int,
    ): List<DayLesson> {
        var regularLesson = regularLesson
        var date = dateFrom + dayMs
        val result = mutableListOf<DayLesson>()
        var count = 0
        var hasPotentialLessons: Boolean
        do {
            val res = calculateDayLessons(regularLesson, date, date + 1)
            if (res.isNotEmpty()) {
                result.addAll(res)
                count++
                if (count == n) break
            }
            date += dayMs
            regularLesson = regularLesson.filter {(it.dateTo ?: 0) >= date }
            hasPotentialLessons = regularLesson.isNotEmpty()
        } while (hasPotentialLessons)

        return result
    }

}