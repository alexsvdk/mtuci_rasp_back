package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.codecs.pojo.annotations.BsonId
import ru.mtuci.di.getRepository
import ru.mtuci.di.koin

@GraphQLDescription("Занятие, которое состоится в конкретный день, в конкретное время")
data class DayLesson(
    val startTime: Long,
    val endTime: Long,
    val regularLessonId: String,
) {

    @JsonIgnore
    @BsonId
    @GraphQLName("regularLesson")
    fun getRegularLesson() = koin.getRepository<RegularLesson>().get(regularLessonId)

}