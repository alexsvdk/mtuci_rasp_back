package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.calculators.DayLessonsCalculator
import ru.mtuci.core.LessonsRepository
import ru.mtuci.di.koin
import ru.mtuci.models.DayLesson
import ru.mtuci.models.LessonsPagination
import ru.mtuci.models.SearchFilter
import java.util.*

@Component
class LessonsQuery : Query {

    private val regularRepo: LessonsRepository = koin.get()

    fun findRegularLessons(
        searchFilter: SearchFilter,
        offset: Int? = 0,
        limit: Int? = 50,
        from: Long? = null,
        to: Long? = null,
    ): LessonsPagination {
        if ((offset ?: -1) < 0)
            throw IllegalArgumentException("Offset must be positive")
        if ((limit ?: -1) < 0)
            throw IllegalArgumentException("Limit must be positive")
        if (searchFilter.isEmpty())
            throw IllegalArgumentException("Search filter must not be empty")

        return regularRepo.findLessons(
            searchFilter, offset!!, limit!!, from, to
        )
    }

    fun findDayLessons(
        searchFilter: SearchFilter,
        startDate: Long?,
        endDate: Long?,
    ): List<DayLesson> {
        val startDate = startDate ?: Date().time
        val endDate = endDate ?: (startDate + 1)

        if (startDate > endDate)
            throw IllegalArgumentException("Start date must be less than end date")
        if (searchFilter.isEmpty())
            throw IllegalArgumentException("Search filter must not be empty")

        val lessons = regularRepo.findLessons(
            searchFilter, from = startDate, to = endDate,
        )

        return DayLessonsCalculator.calculateDayLessons(lessons.data, startDate, endDate)
    }

    fun findDayLessonsForNextDays(
        searchFilter: SearchFilter,
        days: Int? = null,
        startDate: Long?,
    ): List<DayLesson> {
        val days = days ?: 1
        val startDate = startDate ?: Date().time

        if (startDate < 0)
            throw IllegalArgumentException("Start date must be positive")
        if (searchFilter.isEmpty())
            throw IllegalArgumentException("Search filter must not be empty")

        val lessons = regularRepo.findLessons(
            searchFilter, from = startDate
        )

        return DayLessonsCalculator.calculateDayLessonsForNextDays(lessons.data, startDate, days)
    }


    fun regularLessonById(
        id: String,
    ) =
        regularRepo.get(id)
}