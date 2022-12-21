package ru.mtuci.parser.rasp

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import ru.mtuci.core.DirectionsRepository
import ru.mtuci.core.GroupsRepository
import ru.mtuci.models.Direction
import ru.mtuci.models.Group
import save
import java.util.*
import java.util.logging.Logger

class RaspParserV1(
    private val groupsRepo: GroupsRepository,
    private val directionsRepo: DirectionsRepository,

    ) : RaspParser {
    override fun canParse(sheet: Sheet): Boolean {
        val str = sheet.getRow(7).getCell(0).stringCellValue
        return str.contains("Расписание", ignoreCase = true)
    }

    override fun parse(sheet: Sheet, logger: Logger): RaspParseResult {
        val group = getGroupByName(sheet.sheetName)
        val direction = getDirectionByName(sheet.getRow(10).getCell(0).stringCellValue)

        group.directionId = direction.id
        val matches =
            RaspParserConstants.dateRex.findAll(sheet.getRow(12).getCell(0).stringCellValue).map { it.value }
                .toList()
        val termStartDate = matches.firstOrNull()?.let {
            RaspParserConstants.dateFormat.parse(it).time
        }
        val termEndDate = matches.elementAtOrNull(1)?.let {
            RaspParserConstants.dateFormat.parse(it).time
        }

        group.save()


        if (direction.codeName == null) {
            direction.codeName = group.name?.filter { !it.isDigit() }
            direction.save()
        }

        logger.info("Парсинг расписания для группы ${group.name}")
        if (termStartDate != null && termEndDate != null)
            logger.info(
                "Семестр: ${RaspParserConstants.dateFormat.format(Date(termStartDate))} - ${
                    RaspParserConstants.dateFormat.format(
                        Date(termEndDate)
                    )
                }"
            )

        val rawLessons = mutableListOf<RawRepeatedLesson>()

        for (day in 0..5) for (lessonNum in 0..4) {
            rawLessons.addAll(
                parseRawLessons(sheet.getRow(16 + 5 * day + lessonNum), day),
            )
        }

        val lessons = rawLessons.map {
            it.group = group
            it.buildLesson()
        }


        return RaspParseResult(lessons, group, termStartDate, termEndDate)
    }

    private fun parseRawLessons(row: Row, day: Int): List<RawRepeatedLesson> {

        //left
        val raw1 = try {
            RawRepeatedLesson(day).apply {
                name = row.getCell(6).stringCellValue.trim()
                teacher = row.getCell(5).stringCellValue.trim()
                type = row.getCell(4).stringCellValue.trim()
                num = row.getCell(1).numericCellValue.toInt()
                room = row.getCell(3).stringCellValue.trim()
            }
        } catch (e: Exception) {
            null
        }

        //right
        val raw2 = try {
            RawRepeatedLesson(day + 7).apply {
                name = row.getCell(7).stringCellValue.trim()
                teacher = row.getCell(8).stringCellValue.trim()
                type = row.getCell(9).stringCellValue.trim()
                num = row.getCell(1).numericCellValue.toInt()
                room = row.getCell(10).stringCellValue.trim()
            }
        } catch (e: Exception) {
            null
        }

        return mutableListOf<RawRepeatedLesson>().apply {
            if (raw1?.name?.isNotEmpty() == true) add(raw1)
            if (raw2?.name?.isNotEmpty() == true) add(raw2)
        }
    }

    private fun getDirectionByName(name: String): Direction {
        val code = name.substringBefore(" ")
        return directionsRepo.findByCode(code) ?: Direction().let {
            it.code = code
            it.name = name.substringAfter(" ")
            directionsRepo.save(it)
        }
    }

    private fun getGroupByName(name: String): Group {
        return groupsRepo.findByName(name) ?: Group().let {
            it.name = name
            groupsRepo.save(it)
        }
    }


}