package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.di.koin
import ru.mtuci.models.DisciplinesPagination

@Component
class DisciplinesQuery : Query {

    private val repo: DisciplinesRepository = koin.get()

    fun findDisciplines(
        search: String? = null,
        offset: Int? = 0,
        limit: Int? = 50,
    ): DisciplinesPagination {
        assert((offset ?: -1) >= 0)
        assert((limit ?: -1) > 0)

        return repo.softSearch(
            search, offset!!, limit!!
        )
    }

}