package ru.mtuci.calculators

import ru.mtuci.models.lessons.BaseLesson
import java.util.*

object LessonDatesCalculator {

    private val fromRex = "/с ([0-9]+)/".toRegex()
    private val toRex = "до ([0-9]+)/".toRegex()

    

    /// Находит понедельник для даты
    fun calculateMonday(date: Long): Long {
        val cal = GregorianCalendar(DayLessonsCalculator.timeZone)
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.timeInMillis = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val isWeekend = dayOfWeek > 5
        cal.add(Calendar.DATE, if (isWeekend) 8-dayOfWeek else -dayOfWeek+1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis + cal.timeZone.rawOffset
    }

    fun calculateLessonStartDate(lesson: BaseLesson) {
        val firstMonday = calculateMonday(lesson.termStartDate!!)

        //todo

        val fromWeek =
            lesson.tags.map { fromRex.find(it) }.filterNotNull().firstOrNull()?.groupValues?.firstOrNull()?.toInt()
        val toWeek =
            lesson.tags.map { toRex.find(it) }.filterNotNull().firstOrNull()?.groupValues?.firstOrNull()?.toInt()
    }

}