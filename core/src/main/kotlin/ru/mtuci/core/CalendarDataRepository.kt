package ru.mtuci.core

import ru.mtuci.models.CalendarData
import ru.mtuci.models.SearchFilter

interface CalendarDataRepository : BaseRepository<CalendarData> {
    fun findBySearchFilter(searchFilter: SearchFilter): CalendarData?

    fun findByAnyOf(
        teachers: List<String>,
        disciplines: List<String>,
        rooms: List<String>,
        groups: List<String>,
    ): List<CalendarData>

}