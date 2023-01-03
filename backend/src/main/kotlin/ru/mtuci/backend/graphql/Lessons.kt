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
        from: Long? = null,
        to: Long? = null
    ): RegularLessonsPagination {
        assert((offset ?: -1) >= 0)
        assert((limit ?: -1) > 0)
        return regularRepo.findRegularLessons(
            groupId,
            teacherId,
            disciplineId,
            roomId,
            lessonType,
            offset!!, limit!!, from, to,
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
            groupId = groupId,
            teacherId = teacherId,
            disciplineId = disciplineId,
            roomId = roomId,
            offset = 0, limit = 65,
            from = startDate,
            to = endDate,
        )

        return DayLessonsCalculator.calculateDayLessons(lessons.data, startDate, endDate)
    }

    fun findDayLessonsForNextDays(
        days: Int? = null,
        startDate: Long?,
        groupId: String? = null,
        teacherId: String? = null,
        disciplineId: String? = null,
        roomId: String? = null,
    ): List<DayLesson> {
        val days = days ?: 1
        val startDate = startDate ?: Date().time

        assert(days > 0)
        assert(startDate > 0)

        val lessons = regularRepo.findRegularLessons(
            groupId = groupId,
            teacherId = teacherId,
            disciplineId = disciplineId,
            roomId = roomId,
            offset = 0, limit = 65,
            from = startDate,
        )

        return DayLessonsCalculator.calculateDayLessonsForNextDays(lessons.data, startDate, days)
    }


    fun dayLessonById(
        id: String,
    ) =
        regularRepo.get(id)
}