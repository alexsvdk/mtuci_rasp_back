package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.fasterxml.jackson.annotation.JsonProperty
import org.bson.codecs.pojo.annotations.BsonId

open class BaseDocument {

    @BsonId
    @JsonProperty("_id")
    @GraphQLDescription("Document id")
    var id: String? = null

}