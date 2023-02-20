package ru.mtuci.parser.rasp.parsers

import org.apache.poi.ss.usermodel.Sheet
import ru.mtuci.models.lessons.BaseLesson
import ru.mtuci.parser.rasp.RaspParseResult
import java.util.logging.Logger

interface RaspParser<T : BaseLesson> {

    fun canParse(sheet: Sheet): Boolean

    fun parse(fileName: String, sheet: Sheet, logger: Logger): RaspParseResult<T>

}