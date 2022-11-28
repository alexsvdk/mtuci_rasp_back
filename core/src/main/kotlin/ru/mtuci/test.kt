import ru.mtuci.core.GroupsRepository
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.di.koin
import ru.mtuci.utils.DayLessonsCalculator
import java.util.*

fun main() {
    val groupsRepo = koin.get<GroupsRepository>()
    val lessonsRepo = koin.get<RegularLessonsRepository>()

    val group = groupsRepo.findByName("БВТ1902") ?: return
    val lessons = lessonsRepo.findRegularLessons(groupId = group.id, offset = 0, limit = 100)?.data;

    val now = Date().time;

    val res = DayLessonsCalculator.calculateDayLessons(
        lessons,
        now, now, group,
    )
    res;
}