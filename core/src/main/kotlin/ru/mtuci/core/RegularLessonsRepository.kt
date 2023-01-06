package ru.mtuci.core

import ru.mtuci.models.LessonType
import ru.mtuci.models.RegularLesson
import ru.mtuci.models.RegularLessonsPagination
import ru.mtuci.models.SearchFilter

interface RegularLessonsRepository : BaseRepository<RegularLesson> {

    fun findRegularLessons(
        groupId: String? = null,
        teacherId: String? = null,
        disciplineId: String? = null,
        roomId: String? = null,
        lessonType: LessonType? = null,
        offset: Int = 0,
        limit: Int = 1000,
        from: Long? = null,
        to: Long? = null
    ): RegularLessonsPagination

    fun findRegularLessons(
        searchFilter: SearchFilter,
        offset: Int = 0,
        limit: Int = 1000,
        from: Long? = null,
        to: Long? = null
    ) = findRegularLessons(
        groupId = searchFilter.groupId,
        teacherId = searchFilter.teacherId,
        disciplineId = searchFilter.disciplineId,
        roomId = searchFilter.roomId,
        lessonType = searchFilter.lessonType,
        offset = offset,
        limit = limit,
        from = from,
        to = to,
    )

    fun findClone(lesson: RegularLesson): RegularLesson?

}