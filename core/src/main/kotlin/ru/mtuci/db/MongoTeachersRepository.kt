package ru.mtuci.db

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters.regex
import org.bson.conversions.Bson
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.findOne
import org.litote.kmongo.path
import ru.mtuci.core.TeachersRepository
import ru.mtuci.models.Teacher
import ru.mtuci.models.TeachersPagination

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

    override fun softSearch(search: String?, offset: Int, limit: Int): TeachersPagination {
        val filters = mutableListOf<Bson>()

        search?.let {
            val names = it.split(" ")

            filters.add(
                regex(
                    Teacher::lastName.path(),
                    names[0],
                    "i"
                )
            )
        }

        val findRes = if (filters.isNotEmpty()) collection.find(and(filters)) else collection.find()
        val total = findRes.count()
        return TeachersPagination(total, findRes.skip(offset).limit(limit).toList())
    }

}