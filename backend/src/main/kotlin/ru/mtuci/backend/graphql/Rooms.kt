package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.core.RoomsRepository
import ru.mtuci.di.koin
import ru.mtuci.models.RoomsPagination

@Component
class RoomsQuery : Query {

    private val repo: RoomsRepository = koin.get()

    fun roomById(id: String) = repo.get(id)

    fun findRooms(
        search: String? = null,
        offset: Int? = 0,
        limit: Int? = 50,
    ): RoomsPagination {
        assert((offset ?: -1) >= 0)
        assert((limit ?: -1) > 0)

        return repo.softSearch(search, offset!!, limit!!)
    }

}