package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.codecs.pojo.annotations.BsonIgnore
import ru.mtuci.di.getRepository
import ru.mtuci.di.koin

class RegularLesson : BaseDocument() {

    var lessonType = LessonType.UNKNOWN
    var dateFrom: Long? = null
    var dateTo: Long? = null
    var teacherId: String? = null
    var tags = mutableListOf<String>()

    @GraphQLDescription("Day in two weeks [0-13]")
    var tweekDay: Int? = null

    @GraphQLDescription("Lesson number in day")
    var lessonNum: Int? = null

    @JsonIgnore
    @BsonIgnore
    @GraphQLName("teacher")
    fun getTeacher(): Teacher? = teacherId?.let(koin.getRepository<Teacher>()::get)

    var roomId: String? = null

    @JsonIgnore
    @BsonIgnore
    @GraphQLName("room")
    fun getRoom(): Room? = roomId?.let(koin.getRepository<Room>()::get)

    var groupIds = mutableListOf<String>()

    @JsonIgnore
    @BsonIgnore
    @GraphQLName("groups")
    fun getGroups(): List<Group> = koin.getRepository<Group>().getMany(groupIds)

    var disciplineId: String? = null

    @JsonIgnore
    @BsonIgnore
    @GraphQLName("discipline")
    fun getDiscipline(): Discipline? = disciplineId?.let(koin.getRepository<Discipline>()::get)

}
