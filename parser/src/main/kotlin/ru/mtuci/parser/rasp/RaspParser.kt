package ru.mtuci.parser.rasp

import org.apache.poi.ss.usermodel.Sheet
import java.util.logging.Logger

interface RaspParser {

    fun canParse(sheet: Sheet): Boolean

    fun parse(sheet: Sheet, logger: Logger): RaspParseResult

}