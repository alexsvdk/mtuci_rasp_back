package ru.mtuci.parser.rasp

import java.text.SimpleDateFormat

object RaspParserConstants {
    val dateRex = "[0-9]{2}.[0-9]{2}.[0-9]{4}".toRegex()
    val dateFormat = SimpleDateFormat("dd.MM.yyyy")
    val innerRex = "\\([^]]+\\)".toRegex()
    val ends = listOf("по", "до")
    val starts = listOf("с")
    val dateFRex = "[0-9]{2}.[0-9]{2}.".toRegex()
    val fioRex = "([a-zA-ZА-Яа-я,ё]*) ([a-zA-ZА-Яа-я,ё])\\.([a-zA-ZА-Яа-я,ё])\\.".toRegex()
}