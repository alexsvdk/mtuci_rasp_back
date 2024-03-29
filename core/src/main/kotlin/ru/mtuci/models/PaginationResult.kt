package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import ru.mtuci.models.lessons.BaseLesson

@GraphQLIgnore
sealed class PaginationResult<T>(
    val total: Int,
    val data: List<T>,
)

class LessonsPagination(total: Int, data: List<BaseLesson>) : PaginationResult<BaseLesson>(total, data)
class TeachersPagination(total: Int, data: List<Teacher>) : PaginationResult<Teacher>(total, data)
class GroupsPagination(total: Int, data: List<Group>) : PaginationResult<Group>(total, data)
class RoomsPagination(total: Int, data: List<Room>) : PaginationResult<Room>(total, data)
class DisciplinesPagination(total: Int, data: List<Discipline>) : PaginationResult<Discipline>(total, data)