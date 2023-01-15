package ru.mtuci.calculators

import ru.mtuci.core.DisciplinesRepository
import ru.mtuci.core.GroupsRepository
import ru.mtuci.core.RoomsRepository
import ru.mtuci.core.TeachersRepository
import ru.mtuci.models.SearchFilter

class FilterHashCalculator(
    private val groupsRepository: GroupsRepository,
    private val teachersRepository: TeachersRepository,
    private val roomsRepository: RoomsRepository,
    private val disciplinesRepository: DisciplinesRepository,
) {
    fun getFilterHash(filter: SearchFilter): Int {
        val group = filter.groupId?.let(groupsRepository::get)
        val teacher = filter.teacherId?.let(teachersRepository::get)
        val room = filter.roomId?.let(roomsRepository::get)
        val discipline = filter.disciplineId?.let(disciplinesRepository::get)
        val lessonType = filter.lessonType

        return listOf(group?.revision, teacher?.revision, room?.revision, discipline?.revision, lessonType)
            .hashCode()
    }
}