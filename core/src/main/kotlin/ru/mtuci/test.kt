
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.di.koin

fun main() {
    val lessonsRepo = koin.get<RegularLessonsRepository>()

    lessonsRepo.removeAll()
}