package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.core.GroupsRepository
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.di.koin
import ru.mtuci.models.DayLesson
import ru.mtuci.models.LessonType
import ru.mtuci.models.RegularLessonsPagination
import ru.mtuci.utils.DayLessonsCalculator
import java.util.*

@Component
class LessonsQuery : Query {

    private val regularRepo: RegularLessonsRepository = koin.get()
    private val groupRepo: GroupsRepository = koin.get()

    fun findRegularLessons(
        groupId: String? = null,
        teacherId: String? = null,
        disciplineId: String? = null,
        roomId: String? = null,
        lessonType: LessonType? = null,
        offset: Int? = 0,
        limit: Int? = 50,
    ): RegularLessonsPagination {
        assert((offset ?: -1) >= 0)
        assert((limit ?: -1) > 0)
        return regularRepo.findRegularLessons(
            groupId,
            teacherId,
            disciplineId,
            roomId,
            lessonType,
            offset!!, limit!!
        )
    }

    fun findDayLessons(
        startDate: Long? = null,
        endDate: Long? = null,
        groupId: String? = null,
        teacherId: String? = null,
        disciplineId: String? = null,
        roomId: String? = null,
    ): List<DayLesson> {
        val startDate = startDate ?: Date().time
        val endDate = endDate ?: (startDate + 1)

        assert(groupId != null || teacherId != null || disciplineId != null || roomId != null)
        assert(startDate < endDate)

        val lessons = regularRepo.findRegularLessons(
            groupId,
            teacherId,
            disciplineId,
            roomId,
            null,
            0, 65
        )

        val group = groupId?.let(groupRepo::get)

        return DayLessonsCalculator.calculateDayLessons(lessons.data, startDate, endDate, group)

    }

    fun dayLessonById(
        id: String,
    ) =
        regularRepo.get(id)
}