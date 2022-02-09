package ru.mtuci.core


import ru.mtuci.models.Room

interface RoomsRepository : BaseRepository<Room> {

    fun findByNumber(number: String): Room?

}