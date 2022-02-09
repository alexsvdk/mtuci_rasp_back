package ru.mtuci.core

import ru.mtuci.models.Group
import ru.mtuci.models.GroupsPagination

interface GroupsRepository : BaseRepository<Group> {

    fun findByName(name: String): Group?

    fun softSearch(
        search: String? = null,
        directionId: String? = null,
        offset: Int = 0,
        limit: Int = 50,
    ):GroupsPagination

}