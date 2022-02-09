package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.models.RegularLesson

class MongoRegularLessonsRepository(database: MongoDatabase) :
    MongoBaseRepository<RegularLesson>(database, RegularLesson::class.java),
    RegularLessonsRepository {


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
                RegularLesson::isDistant eq lesson.isDistant
            )
        }
    }

}