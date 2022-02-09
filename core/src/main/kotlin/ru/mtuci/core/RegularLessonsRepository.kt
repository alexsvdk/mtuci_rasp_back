package ru.mtuci.core

import ru.mtuci.models.LessonType
import ru.mtuci.models.RegularLesson
import ru.mtuci.models.RegularLessonsPagination

interface RegularLessonsRepository: BaseRepository<RegularLesson> {

    fun findRegularLessons(
        groupId: String?,
        teacherId: String?,
        disciplineId: String?,
        roomId: String?,
        lessonType: LessonType?,
        offset: Int,
        limit: Int,
    ): RegularLessonsPagination

    fun findClone(lesson: RegularLesson): RegularLesson?

}