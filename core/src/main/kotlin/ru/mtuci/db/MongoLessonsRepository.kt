package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.bson.conversions.Bson
import org.litote.kmongo.*
import ru.mtuci.calculators.DayLessonsCalculator
import ru.mtuci.core.LessonsRepository
import ru.mtuci.models.LessonType
import ru.mtuci.models.LessonsPagination
import ru.mtuci.models.lessons.BaseLesson
import ru.mtuci.models.lessons.RegularLesson

class MongoLessonsRepository(database: MongoDatabase) :
    MongoBaseRepository<BaseLesson>(database, BaseLesson::class.java),
    LessonsRepository {

    override fun findLessons(
        groupId: String?,
        teacherId: String?,
        disciplineId: String?,
        roomId: String?,
        lessonType: LessonType?,
        offset: Int,
        limit: Int,
        from: Long?,
        to: Long?
    ): LessonsPagination {
        val filters = mutableListOf<Bson>()
        groupId?.let { filters.add(RegularLesson::groupIds contains it) }
        teacherId?.let { filters.add(RegularLesson::teacherId eq it) }
        disciplineId?.let { filters.add(RegularLesson::disciplineId eq it) }
        roomId?.let { filters.add(RegularLesson::roomId eq it) }
        lessonType?.let { filters.add(RegularLesson::lessonType eq it) }
        from?.let { filters.add(RegularLesson::dateTo gte (it - DayLessonsCalculator.dayMs)) }
        to?.let { filters.add(RegularLesson::dateFrom lte (it + DayLessonsCalculator.dayMs)) }

        val findRes = if (filters.isNotEmpty()) collection.find(and(filters)) else collection.find()
        val total = findRes.count()
        return LessonsPagination(total, findRes.skip(offset).limit(limit).toList())
    }


    override fun findClone(lesson: BaseLesson): BaseLesson? {
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