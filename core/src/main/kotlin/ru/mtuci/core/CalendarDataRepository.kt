package ru.mtuci.core

import ru.mtuci.models.CalendarData
import ru.mtuci.models.SearchFilter

interface CalendarDataRepository : BaseRepository<CalendarData> {

    fun findBySearchFilter(searchFilter: SearchFilter): CalendarData?

}