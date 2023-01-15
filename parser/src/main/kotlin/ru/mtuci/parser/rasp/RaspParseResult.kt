package ru.mtuci.parser.rasp

import ru.mtuci.models.Discipline
import ru.mtuci.models.Group
import ru.mtuci.models.Room
import ru.mtuci.models.Teacher
import ru.mtuci.models.common.BaseDocument
import ru.mtuci.models.lessons.BaseLesson
import save

data class RaspParseResult<T : BaseLesson>(
    val lessons: List<T>,
    val group: Group,
    val termStartDate: Long?,
    val termEndDate: Long?,
    val affectedTeachers: List<Teacher>,
    val affectedRooms: List<Room>,
    val afectedDisciplines: List<Discipline>,
) {

    fun incAllRevisions() {
        group.incrementRevision()
        group.save()
        affectedTeachers.forEach {
            it.incrementRevision()
            it.save()
        }
        affectedRooms.forEach {
            it.incrementRevision()
            it.save()
        }
        afectedDisciplines.forEach {
            it.incrementRevision()
            it.save()
        }
    }

    companion object {
        val EMPTY = RaspParseResult(emptyList(), Group(), null, null, emptyList(), emptyList(), emptyList())

        fun <T : BaseLesson> fromBuildRes(
            res: List<BuildLessonResult<T>>,
            group: Group,
            termStartDate: Long? = null,
            termEndDate: Long? = null
        ): RaspParseResult<T> {
            val minDate = res.mapNotNull { it.lesson?.dateFrom }.minOrNull()
            val maxDate = res.mapNotNull { it.lesson?.dateTo }.maxOrNull()
            val lessons = res.mapNotNull { it.lesson }.distinctById()
            val teachers = res.mapNotNull { it.teacher }.distinctById()
            val rooms = res.mapNotNull { it.room }.distinctById()
            val disciplines = res.mapNotNull { it.discipline }.distinctById()
            return RaspParseResult(
                lessons,
                group,
                termStartDate ?: minDate,
                termEndDate ?: maxDate,
                teachers,
                rooms,
                disciplines
            )
        }
    }
}

private fun <T : BaseDocument> Iterable<T>.distinctById() = distinctBy { it.id }