package ru.mtuci.calculators

import ru.mtuci.models.DayLesson
import ru.mtuci.models.lessons.BaseLesson
import ru.mtuci.models.lessons.RegularLesson
import java.util.*

object DayLessonsCalculator {

    private const val minuteMs: Long = 60 * 1000
    private const val hourMs: Long = 60 * minuteMs
    const val dayMs: Long = 24 * hourMs
    val timeZone = TimeZone.getTimeZone("GMT+3:00")

    private val pairsDur = listOf(
        Pair(9 * hourMs + 30 * minuteMs, 11 * hourMs + 5 * minuteMs),
        Pair(11 * hourMs + 20 * minuteMs, 12 * hourMs + 50 * minuteMs),
        Pair(13 * hourMs + 10 * minuteMs, 14 * hourMs + 45 * minuteMs),
        Pair(15 * hourMs + 25 * minuteMs, 17 * hourMs),
        Pair(17 * hourMs + 15 * minuteMs, 18 * hourMs + 50 * minuteMs),
    )

    fun calculateDayLessons(
        regularLesson: List<BaseLesson>,
        dateFrom: Long,
        dateTo: Long?,
    ): List<DayLesson> {
        val dateTo = dateTo ?: Long.MAX_VALUE
        val res = mutableListOf<DayLesson>()

        for (lesson in regularLesson) {
            val id = lesson.id ?: continue
            if ((lesson.dateFrom ?: continue) > dateTo) continue
            if ((lesson.dateTo ?: continue) < dateFrom) continue

            val dateFrom = maxOf(lesson.dateFrom!!, dateFrom)
            val dateTo = minOf(lesson.dateTo!!, dateTo)

            val firstMonday = LessonDatesCalculator.calculateMonday(dateFrom)

            if (lesson is RegularLesson) {
                for (dateTime in dateFrom..dateTo step dayMs) {
                    val dateWithoutTime = dateTime - dateTime % dayMs
                    var tweekDay = (((dateWithoutTime - firstMonday) / dayMs) % 14).toInt()


                    if (tweekDay < -2) {
                        println("WARNING tweekDay < -2")
                        /*
                        cal.add(Calendar.WEEK_OF_YEAR, -1)
                        timezonedCalTime = cal.timeInMillis + cal.timeZone.rawOffset
                        tweekDay = (((dateTime - timezonedCalTime) / dayMs) % 14).toInt()
                         */
                    }


                    if (tweekDay == lesson.tweekDay) {

                        val dur = lesson.lessonNum?.let { pairsDur[it - 1] } ?: continue
                        res.add(
                            DayLesson(
                                dateWithoutTime + dur.first,
                                dateWithoutTime + dur.second,
                                id,
                            )
                        )
                    }

                }
            } else {
                if (dateFrom == lesson.dateFrom) {
                    res.add(
                        DayLesson(
                            lesson.dateFrom!!,
                            lesson.dateTo!!,
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
        regularLesson: List<BaseLesson>,
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
            regularLesson = regularLesson.filter { (it.dateTo ?: 0) >= date }
            hasPotentialLessons = regularLesson.isNotEmpty()
        } while (hasPotentialLessons)

        return result
    }

    fun calculateFirstLesson(
        lesson: BaseLesson
    ): DayLesson? {
        val dateFrom = (lesson.dateFrom ?: return null) - dayMs
        val rawRes = calculateDayLessonsForNextDays(listOf(lesson), dateFrom, 1)
        return rawRes.firstOrNull()
    }

    fun calculateLastLesson(
        lesson: BaseLesson
    ): DayLesson? {
        val dateTo = lesson.dateTo ?: return null
        val lastTweekDate = dateTo - dayMs * 14
        val rawRes = calculateDayLessons(listOf(lesson), lastTweekDate, dateTo)
        return rawRes.lastOrNull()
    }

}