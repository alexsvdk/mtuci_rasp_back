package ru.mtuci.core

import ru.mtuci.models.Group

interface GroupsRepository : BaseRepository<Group> {

    fun findByName(name: String): Group?

}