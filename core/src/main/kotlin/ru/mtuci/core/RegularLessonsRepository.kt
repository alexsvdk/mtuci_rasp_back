package ru.mtuci.core

import ru.mtuci.models.RegularLesson

interface RegularLessonsRepository: BaseRepository<RegularLesson> {

    fun findClone(lesson: RegularLesson): RegularLesson?

}