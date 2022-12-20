package ru.mtuci.parser.rasp

import ru.mtuci.models.Group
import ru.mtuci.models.RegularLesson

data class RaspParseResult(
    val lessons: List<RegularLesson>,
    val group: Group,
)