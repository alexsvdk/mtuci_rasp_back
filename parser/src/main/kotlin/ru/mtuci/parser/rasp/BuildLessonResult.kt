package ru.mtuci.parser.rasp

import ru.mtuci.models.Discipline
import ru.mtuci.models.Room
import ru.mtuci.models.Teacher
import ru.mtuci.models.lessons.BaseLesson

class BuildLessonResult<T: BaseLesson> {
    var teacher: Teacher? = null
    var discipline: Discipline? = null
    var room: Room? = null
    var lesson: T? = null
}