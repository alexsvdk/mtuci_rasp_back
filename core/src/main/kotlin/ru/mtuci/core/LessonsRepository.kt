package ru.mtuci.core

import ru.mtuci.models.LessonType
import ru.mtuci.models.LessonsPagination
import ru.mtuci.models.SearchFilter
import ru.mtuci.models.lessons.BaseLesson

interface LessonsRepository : BaseRepository<BaseLesson> {

    fun findLessons(
        groupId: String? = null,
        teacherId: String? = null,
        disciplineId: String? = null,
        roomId: String? = null,
        lessonType: LessonType? = null,
        offset: Int = 0,
        limit: Int = 1000,
        from: Long? = null,
        to: Long? = null
    ): LessonsPagination

    fun findLessons(
        searchFilter: SearchFilter,
        offset: Int = 0,
        limit: Int = 1000,
        from: Long? = null,
        to: Long? = null
    ) = findLessons(
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

    fun findClone(lesson: BaseLesson): BaseLesson?

}