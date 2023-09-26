import org.junit.jupiter.api.Test
import ru.mtuci.calculators.DayLessonsCalculator
import ru.mtuci.calculators.LessonDatesCalculator
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LessonDatesCalculatorTest {
    @Test
    fun earlyTermStartDateTest() {
        // 01.09.2023
        val date = 1693515600000
        val monday = LessonDatesCalculator.calculateMonday(date)

        val cal = GregorianCalendar(DayLessonsCalculator.timeZone)
        cal.timeInMillis = monday
        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK), "earlyTermStartDateTest")
        assertTrue { monday < date }
    }

    @Test
    fun lateTermStartDateTest() {
        // 02.09.2023
        val date = 1693602000000
        val monday = LessonDatesCalculator.calculateMonday(date)

        // validate is monday
        val cal = GregorianCalendar(DayLessonsCalculator.timeZone)
        cal.timeInMillis = monday
        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK), "lateTermStartDateTest")
        assertTrue { monday > date }
    }

    @Test
    fun lateTermStartDateTest2() {
        // 03.09.2023
        val date = 1693688400000
        val monday = LessonDatesCalculator.calculateMonday(date)

        // validate is monday
        val cal = GregorianCalendar(DayLessonsCalculator.timeZone)
        cal.timeInMillis = monday
        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK), "lateTermStartDateTest")
        assertTrue { monday > date }
    }
}