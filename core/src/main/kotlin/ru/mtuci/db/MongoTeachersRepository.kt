package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import ru.mtuci.core.TeachersRepository
import ru.mtuci.models.Teacher

class MongoTeachersRepository(database: MongoDatabase) : MongoBaseRepository<Teacher>(database, Teacher::class.java),
    TeachersRepository {

    override fun findByLastNameAndInitials(lastName: String, firstI: String?, fathersI: String?): Teacher? {
        return collection.findOne {
            and(
                Teacher::lastName eq lastName,
                Teacher::firstNameI eq firstI,
                Teacher::fathersNameI eq fathersI,
            )
        }
    }


}