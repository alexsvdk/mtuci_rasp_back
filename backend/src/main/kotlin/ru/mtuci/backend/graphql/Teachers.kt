package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.core.TeachersRepository
import ru.mtuci.di.koin
import ru.mtuci.models.TeachersPagination

@Component
class TeachersQuery : Query {

    private val repo: TeachersRepository = koin.get()

    fun findTeachers(
        search: String? = null,
        offset: Int? = 0,
        limit: Int? = 50,
    ): TeachersPagination {
        assert((offset ?: -1) >= 0)
        assert((limit ?: -1) > 0)
        return repo.softSearch(
            search, offset!!, limit!!
        )
    }


}