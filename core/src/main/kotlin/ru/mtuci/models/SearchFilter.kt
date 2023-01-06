package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("Filter used to search lessons")
data class SearchFilter(
    @GraphQLDescription("Search by group")
    var groupId: String? = null,

    @GraphQLDescription("Search by teacher")
    var teacherId: String? = null,

    @GraphQLDescription("Search by room")
    var roomId: String? = null,

    @GraphQLDescription("Search by discipline")
    var disciplineId: String? = null,

    @GraphQLDescription("Search by lesson type")
    var lessonType: LessonType? = null,
) {

    fun isEmpty() =
        groupId == null && teacherId == null && roomId == null && disciplineId == null && lessonType == null

}