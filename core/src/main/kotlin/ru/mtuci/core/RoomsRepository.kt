package ru.mtuci.core


import ru.mtuci.models.Room
import ru.mtuci.models.RoomsPagination

interface RoomsRepository : BaseRepository<Room> {

    fun findByNumber(number: String): Room?

    fun softSearch(
        search: String? = null,
        offset: Int = 0,
        limit: Int = 50,
    ): RoomsPagination

}