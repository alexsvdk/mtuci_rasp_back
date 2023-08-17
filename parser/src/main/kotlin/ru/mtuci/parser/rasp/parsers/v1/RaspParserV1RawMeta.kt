package ru.mtuci.parser.rasp.parsers.v1

data class RaspParserV1RawMeta (
    val groupName: String,
    val directionName: String,
    val facultyName: String?,
    val raspStartRow: Int,
    val termStartDate: Long,
    val termEndDate: Long,
)
