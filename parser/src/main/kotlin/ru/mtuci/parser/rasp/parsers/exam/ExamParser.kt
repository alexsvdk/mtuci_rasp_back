package ru.mtuci.parser.rasp.parsers.exam

import org.apache.poi.ss.usermodel.Sheet
import ru.mtuci.core.GroupsRepository
import ru.mtuci.models.lessons.BaseLesson
import ru.mtuci.parser.rasp.BuildLessonResult
import ru.mtuci.parser.rasp.RaspParseResult
import ru.mtuci.parser.rasp.RaspParserConstants
import ru.mtuci.parser.rasp.parsers.RaspParser
import ru.mtuci.parser.rasp.parsers.RawLesson
import java.util.logging.Logger

class ExamParser(
    val groupsRepo: GroupsRepository,
) : RaspParser<BaseLesson> {

    override fun canParse(sheet: Sheet): Boolean {
        val str = sheet.getRow(12).getCell(0).stringCellValue

        val isExam = str.contains("Расписание", ignoreCase = true) && str.contains("Сессии", ignoreCase = true)
        val hasGroup = groupsRepo.findByName(sheet.sheetName) != null

        return isExam && hasGroup
    }

    override fun parse(fileName: String, sheet: Sheet, logger: Logger): RaspParseResult<BaseLesson> {
        val year = sheet.getRow(13).getCell(0).stringCellValue
        val title = sheet.getRow(12).getCell(0).stringCellValue
        val groupName =
            listOf(fileName, sheet.sheetName).find { RaspParserConstants.groupRex.matches(it) }
                ?: throw Exception("Группа не найдена")
        val group = groupsRepo.findByName(groupName) ?: throw Exception("Группа не найдена")
        group.incrementRevision()

        val res = mutableListOf<BuildLessonResult<BaseLesson>>()

        var rowNum = 15
        while (sheet.getRow(rowNum)?.getCell(0)?.stringCellValue?.isBlank() == false) {
            res.add(
                RawLesson().apply {
                    this.group = group
                    date = sheet.getRow(rowNum).getCell(0).stringCellValue
                    name = sheet.getRow(rowNum).getCell(1).stringCellValue
                    teacher = sheet.getRow(rowNum).getCell(2).stringCellValue
                    type = sheet.getRow(rowNum).getCell(3).stringCellValue
                    time = sheet.getRow(rowNum).getCell(4).stringCellValue
                    room = sheet.getRow(rowNum).getCell(5).stringCellValue
                    studyYear = year
                    sheetTitle = title
                }.buildExamLesson(),
            )
            rowNum++
        }

        return RaspParseResult.fromBuildRes(res, group)
    }
}
