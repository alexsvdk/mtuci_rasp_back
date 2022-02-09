package ru.mtuci.backend.graphql

import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.di.koin
import ru.mtuci.models.LessonType
import ru.mtuci.models.RegularLessonsPagination

@Component
class LessonsQuery : Query {

    private val regularRepo: RegularLessonsRepository = koin.get()

    fun findRegularLessons(
        groupId: String? = null,
        teacherId: String? = null,
        disciplineId: String? = null,
        roomId: String? = null,
        lessonType: LessonType? = null,
        offset: Int? = 0,
        limit: Int? = 50,
    ): RegularLessonsPagination {
        assert((offset ?: -1) >= 0)
        assert((limit ?: -1) > 0)
        return regularRepo.findRegularLessons(
            groupId,
            teacherId,
            disciplineId,
            roomId,
            lessonType,
            offset!!, limit!!
        )
    }



}