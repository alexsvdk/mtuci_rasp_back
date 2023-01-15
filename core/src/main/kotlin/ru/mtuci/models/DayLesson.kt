package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.codecs.pojo.annotations.BsonId
import ru.mtuci.di.getRepository
import ru.mtuci.di.koin
import ru.mtuci.models.lessons.BaseLesson

@GraphQLDescription("Занятие, которое состоится в конкретный день, в конкретное время")
data class DayLesson(
    val startTime: Long,
    val endTime: Long,
    val lessonId: String,
) {

    @JsonIgnore
    @BsonId
    @GraphQLName("regularLesson")
    fun getRegularLesson() = koin.getRepository<BaseLesson>().get(lessonId)

    override fun hashCode(): Int {
        var result = startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        result = 31 * result + lessonId.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DayLesson

        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false
        if (lessonId != other.lessonId) return false

        return true
    }

}