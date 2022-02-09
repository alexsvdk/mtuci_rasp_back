package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

@GraphQLIgnore
sealed class PaginationResult<T>(
    val total: Int,
    val data: List<T>,
)

class RegularLessonsPagination(total: Int, data: List<RegularLesson>) : PaginationResult<RegularLesson>(total, data)