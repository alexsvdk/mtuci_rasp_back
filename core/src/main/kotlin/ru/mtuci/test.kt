import ru.mtuci.core.GroupsRepository
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.di.koin
import ru.mtuci.calculators.DayLessonsCalculator
import java.util.*

fun main() {
    val groupsRepository = koin.get<GroupsRepository>()
    val lessonsRepo = koin.get<RegularLessonsRepository>()
    val group = groupsRepository.findByName("БАП2101")

    val from = 1672997804000 // 7 jan 2023
    val to = 1675749675000 // 7 feb 2023

    val lessons = lessonsRepo.findRegularLessons(
        groupId = group!!.id,
        from = from,
        to = to,
    ).data

    val calculatedDayLessons = DayLessonsCalculator.calculateDayLessons(lessons, from, to)
    val calculatedNextLessons = DayLessonsCalculator.calculateDayLessonsForNextDays(lessons, from, 30)
    val allDayLessons = listOf(calculatedDayLessons, calculatedNextLessons).flatten().toSet()

    val sixFebLessons = allDayLessons.filter { it.startTime in 1675630800000..1675717200000 };

    val sundayLessons = allDayLessons.filter {
        val cal = Calendar.getInstance()
        cal.timeInMillis = it.startTime
        cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
    }

    assert(sundayLessons.isEmpty())
    assert(sixFebLessons.mapNotNull { it.getRegularLesson()?.tweekDay }.toSet().size == 1)
}