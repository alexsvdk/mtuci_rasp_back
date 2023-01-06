package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.calculators.FilterHashCalculator
import ru.mtuci.core.CalendarDataRepository
import ru.mtuci.di.koin
import ru.mtuci.models.CalendarData
import ru.mtuci.models.SearchFilter
import save

@Component
class CalendarQuery : Query {

    private val calendarRepo: CalendarDataRepository = koin.get()
    private val filterHashCalculator: FilterHashCalculator = koin.get()

    fun calendarById(id: String): CalendarData? = calendarRepo.get(id)

    fun calendarByFilter(searchFilter: SearchFilter): CalendarData {
        calendarRepo.findBySearchFilter(searchFilter)?.let { return it }

        if (searchFilter.isEmpty())
            throw IllegalArgumentException("Search filter must not be empty")

        val calendar = CalendarData(searchFilter)
        calendar.filtersRevisionsHash = filterHashCalculator.getFilterHash(searchFilter)

        return calendar.save()
    }

}