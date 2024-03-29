package ru.mtuci.models.lessons

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import org.bson.codecs.pojo.annotations.BsonIgnore
import ru.mtuci.di.getRepository
import ru.mtuci.di.koin
import ru.mtuci.models.*
import ru.mtuci.models.common.BaseDocument

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "@class",
)
@JsonSubTypes(
    JsonSubTypes.Type(RegularLesson::class, name = "regular"),
    JsonSubTypes.Type(BaseLesson::class, name = "base"),
)
open class BaseLesson : BaseDocument() {

    @GraphQLDescription("Дата начала занятий")
    var dateFrom: Long? = null

    @GraphQLDescription("Дата начала семестра")
    var termStartDate: Long? = null

    @GraphQLDescription("Дата окончания занятий")
    var dateTo: Long? = null

    @GraphQLDescription("Тип занятия")
    var lessonType = LessonType.UNKNOWN

    @GraphQLDescription("Lesson number in day")
    var lessonNum: Int? = null

    @GraphQLDescription("ID преподавателя")
    var teacherId: String? = null

    @GraphQLDescription("Теги занятия")
    var tags = mutableListOf<String>()

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