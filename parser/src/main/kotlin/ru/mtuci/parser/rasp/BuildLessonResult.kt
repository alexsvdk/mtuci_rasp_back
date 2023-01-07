package ru.mtuci.parser.rasp

import ru.mtuci.models.Discipline
import ru.mtuci.models.RegularLesson
import ru.mtuci.models.Room
import ru.mtuci.models.Teacher

class BuildLessonResult {
    var teacher: Teacher? = null
    var discipline: Discipline? = null
    var room: Room? = null
    var regularLesson: RegularLesson? = null
}