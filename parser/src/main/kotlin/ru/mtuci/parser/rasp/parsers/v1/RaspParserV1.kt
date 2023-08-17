package ru.mtuci.parser.rasp.parsers.v1

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import ru.mtuci.core.DirectionsRepository
import ru.mtuci.core.GroupsRepository
import ru.mtuci.models.Direction
import ru.mtuci.models.Group
import ru.mtuci.models.lessons.RegularLesson
import ru.mtuci.parser.rasp.RaspParseResult
import ru.mtuci.parser.rasp.RaspParserConstants
import ru.mtuci.parser.rasp.parsers.RaspParser
import ru.mtuci.parser.rasp.parsers.RawLesson
import save
import java.util.*
import java.util.logging.Logger

class RaspParserV1(
    private val groupsRepo: GroupsRepository,
    private val directionsRepo: DirectionsRepository,
) : RaspParser<RegularLesson> {
    override fun canParse(sheet: Sheet): Boolean {
        return try {
            val meta = extractRawMeta(sheet)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun extractRawMeta(sheet: Sheet): RaspParserV1RawMeta {
        val baseRowNum = sheet.find {
            it.getCell(0).stringCellValue.contains("Расписание учебных занятий", ignoreCase = true)
        }?.rowNum ?: throw Exception("Не найдена строка с названием факультета")

        val facultyName = sheet.getRow(baseRowNum).getCell(0).stringCellValue
            .replace("Расписание учебных занятий факультета", "")
            .trim()

        val directionName = sheet.getRow(baseRowNum + 3).getCell(0).stringCellValue
            .replace("Направление", "")
            .trim()

        val groupName = sheet.getRow(baseRowNum + 4).getCell(0).stringCellValue
            .replace("Группа", "")
            .trim()

        val matches =
            RaspParserConstants.dateRex.findAll(sheet.getRow(baseRowNum + 5).getCell(0).stringCellValue)
                .map { it.value }
                .toList()

        val termStartDate = matches.firstOrNull()?.let {
            RaspParserConstants.dateFormat.parse(it).time
        }
        val termEndDate = matches.elementAtOrNull(1)?.let {
            RaspParserConstants.dateFormat.parse(it).time
        }

        val raspStartRow = sheet.find {
            it.getCell(0).stringCellValue.contains("Понедельник", ignoreCase = true)
        }?.rowNum ?: throw Exception("Не найдена строка с началом расписания")

        if (directionName.isEmpty() || groupName.isEmpty() || termStartDate == null || termEndDate == null)
            throw Exception("Не удалось извлечь мета-информацию из файла")

        return RaspParserV1RawMeta(
            facultyName = facultyName.takeIf { it.isNotBlank() },
            directionName = directionName,
            groupName = groupName,
            termStartDate = termStartDate,
            termEndDate = termEndDate,
            raspStartRow = raspStartRow,
        )

    }

    override fun parse(fileName: String, sheet: Sheet, logger: Logger): RaspParseResult<RegularLesson> {

        val rawMeta = extractRawMeta(sheet)

        val group = getGroupByName(rawMeta.groupName)
        val direction = getDirectionByName(rawMeta.directionName)
        if (rawMeta.facultyName != null && direction.faculty != rawMeta.facultyName) {
            direction.faculty = rawMeta.facultyName
            direction.save()
        }
        if (direction.codeName == null) {
            direction.codeName = group.name?.filter { !it.isDigit() }
            direction.save()
        }
        group.directionId = direction.id
        group.incrementRevision()
        group.save()


        logger.info("Парсинг расписания для группы ${group.name}")
        logger.info(
            "Семестр: ${RaspParserConstants.dateFormat.format(Date(rawMeta.termStartDate))} - ${
                RaspParserConstants.dateFormat.format(
                    Date(rawMeta.termEndDate)
                )
            }"
        )

        val rawLessons = mutableListOf<RawLesson>()

        for (day in 0..5) for (lessonNum in 0..4) {
            rawLessons.addAll(
                parseRawLessons(sheet.getRow(rawMeta.raspStartRow + 5 * day + lessonNum), day),
            )
        }

        val buildLessonResults = rawLessons.map {
            it.group = group
            it.buildRegularLesson()
        }

        val lessons = buildLessonResults.mapNotNull { it.lesson }

        lessons.forEach {
            it.dateFrom = it.dateFrom ?: rawMeta.termStartDate
            it.dateTo = it.dateTo ?: rawMeta.termEndDate
        }


        return RaspParseResult.fromBuildRes(buildLessonResults, group, rawMeta.termStartDate, rawMeta.termEndDate)
    }

    private fun parseRawLessons(row: Row, day: Int): List<RawLesson> {

        //left
        val raw1 = try {
            RawLesson(day).apply {
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
            RawLesson(day + 7).apply {
                name = row.getCell(7).stringCellValue.trim()
                teacher = row.getCell(8).stringCellValue.trim()
                type = row.getCell(9).stringCellValue.trim()
                num = row.getCell(1).numericCellValue.toInt()
                room = row.getCell(10).stringCellValue.trim()
            }
        } catch (e: Exception) {
            null
        }

        return mutableListOf<RawLesson>().apply {
            if (raw1?.name?.isNotEmpty() == true) add(raw1)
            if (raw2?.name?.isNotEmpty() == true) add(raw2)
        }
    }

    private fun getDirectionByName(name: String): Direction {
        return directionsRepo.findByName(name) ?: Direction().let {
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