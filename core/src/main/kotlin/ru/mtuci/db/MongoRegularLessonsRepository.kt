package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.bson.conversions.Bson
import org.litote.kmongo.*
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.models.LessonType
import ru.mtuci.models.RegularLesson
import ru.mtuci.models.RegularLessonsPagination

class MongoRegularLessonsRepository(database: MongoDatabase) :
    MongoBaseRepository<RegularLesson>(database, RegularLesson::class.java),
    RegularLessonsRepository {

    override fun findRegularLessons(
        groupId: String?,
        teacherId: String?,
        disciplineId: String?,
        roomId: String?,
        lessonType: LessonType?,
        offset: Int,
        limit: Int,
        from: Long?,
        to: Long?
    ): RegularLessonsPagination {
        val filters = mutableListOf<Bson>()
        groupId?.let { filters.add(RegularLesson::groupIds contains it) }
        teacherId?.let { filters.add(RegularLesson::teacherId eq it) }
        disciplineId?.let { filters.add(RegularLesson::disciplineId eq it) }
        roomId?.let { filters.add(RegularLesson::roomId eq it) }
        lessonType?.let { filters.add(RegularLesson::lessonType eq it) }
        from?.let { filters.add(RegularLesson::dateFrom lte it) }
        to?.let { filters.add(RegularLesson::dateTo gte it) }

        val findRes = if (filters.isNotEmpty()) collection.find(and(filters)) else collection.find()
        val total = findRes.count()
        return RegularLessonsPagination(total, findRes.skip(offset).limit(limit).toList())
    }


    override fun findClone(lesson: RegularLesson): RegularLesson? {
        return collection.findOne {
            and(
                RegularLesson::lessonType eq lesson.lessonType,
                RegularLesson::lessonNum eq lesson.lessonNum,
                RegularLesson::dateFrom eq lesson.dateFrom,
                RegularLesson::dateTo eq lesson.dateTo,
                RegularLesson::disciplineId eq lesson.disciplineId,
                RegularLesson::teacherId eq lesson.teacherId,
                RegularLesson::roomId eq lesson.roomId,
            )
        }
    }

}