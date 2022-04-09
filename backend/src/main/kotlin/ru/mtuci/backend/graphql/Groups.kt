package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.core.GroupsRepository
import ru.mtuci.di.koin
import ru.mtuci.models.GroupsPagination

@Component
class GroupsQuery : Query {

    private val repo: GroupsRepository = koin.get()

    fun groupById(id: String) = repo.get(id)

    fun findGroups(
        search: String? = null,
        directionId: String? = null,
        offset: Int? = 0,
        limit: Int? = 50,
    ): GroupsPagination {
        assert((offset ?: -1) >= 0)
        assert((limit ?: -1) > 0)

        return repo.softSearch(
            search, directionId, offset!!, limit!!
        )
    }

}