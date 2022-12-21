package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.fasterxml.jackson.annotation.JsonIgnore
import org.bson.codecs.pojo.annotations.BsonIgnore
import ru.mtuci.di.getRepository
import ru.mtuci.di.koin

@GraphQLDescription("Модель группы")
class Group : BaseDocument() {

    @GraphQLDescription("Название группы")
    var name: String? = null

    var directionId: String? = null

    @JsonIgnore
    @BsonIgnore
    @GraphQLName("direction")
    fun getDirection(): Direction? = directionId?.let(koin.getRepository<Direction>()::get)

}