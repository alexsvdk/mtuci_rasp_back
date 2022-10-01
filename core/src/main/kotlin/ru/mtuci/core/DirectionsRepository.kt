package ru.mtuci.core

import ru.mtuci.models.Direction

interface DirectionsRepository : BaseRepository<Direction> {

    fun findByCode(code: String): Direction?

    fun findByName(name: String): Direction?

}