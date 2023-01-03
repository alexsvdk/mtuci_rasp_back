import ru.mtuci.core.GroupsRepository
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.di.koin
import ru.mtuci.utils.DayLessonsCalculator

fun main() {
    val groupsRepository = koin.get<GroupsRepository>()
    val lessonsRepo = koin.get<RegularLessonsRepository>()

    val group = groupsRepository.findByName("БАП2101")
    val lessons = lessonsRepo.findRegularLessons(group!!.id).data

    DayLessonsCalculator.calculateDayLessons(lessons, 1676454759257, 1676454759257, group)
}