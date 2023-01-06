package ru.mtuci.models.common

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

// Document with revision
@GraphQLIgnore
interface RevisionDocument {

    @GraphQLIgnore
    val revision: Int

    @GraphQLIgnore
    fun incrementRevision()

}