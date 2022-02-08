package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import org.bson.codecs.pojo.annotations.BsonId

open class BaseDocument {

    @BsonId
    @GraphQLDescription("Document id")
    var id: String? = null

}