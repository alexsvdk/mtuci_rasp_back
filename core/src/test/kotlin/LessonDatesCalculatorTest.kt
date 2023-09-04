import org.junit.jupiter.api.Test
import ru.mtuci.calculators.LessonDatesCalculator
import kotlin.test.assertEquals

class LessonDatesCalculatorTest {
    @Test
    fun earlyTermStartDateTest() {
        // 01.09.2022
        val date = 1661979600000
        val monday = LessonDatesCalculator.calculateMonday(date)
        assertEquals(1661644800000, monday, "earlyTermStartDateTest")
    }

    @Test
    fun lateTermStartDateTest() {
        // 02.09.2022
        val date = 1662066000000
        val monday = LessonDatesCalculator.calculateMonday(date)
        assertEquals(1662249600000, monday, "lateTermStartDateTest")
    }
}