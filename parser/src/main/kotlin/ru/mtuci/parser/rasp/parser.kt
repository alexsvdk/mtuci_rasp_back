package ru.mtuci.parser.rasp

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import ru.mtuci.core.DirectionsRepository
import ru.mtuci.core.GroupsRepository
import ru.mtuci.core.RegularLessonsRepository
import ru.mtuci.di.koin
import ru.mtuci.models.Direction
import ru.mtuci.models.Group
import ru.mtuci.parser.rasp.RaspParserConstants.dateFormat
import ru.mtuci.parser.rasp.RaspParserConstants.dateRex
import save
import java.net.URL

private val lessonsRepo = koin.get<RegularLessonsRepository>()

val logger by lazy { LoggerFactory.getLogger("RaspParser") }

fun parseRasp() {
    try {
        logger.info ("Rasp parsing started")
        val urls = getRaspUrls()
        logger.info ("Found ${urls.size} sheets")
        if (urls.isNotEmpty()) {
            lessonsRepo.removeAll()
            urls.forEach(::processTable)
        }
        logger.info ("Rasp parsing finished")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun getRaspUrls(): List<String> {
    val page = Jsoup.connect("https://mtuci.ru/time-table/").get()
    val table = page.select("li > h4 > a")
    return table.map {
        it.selectFirst("a")?.attr("href")
    }.filterNotNull().filter { it.endsWith(".xlsx") }.toList()
}

private fun processTable(url: String) {
    try {
        var url = url
        if (!url.contains("http")) {
            url = "https://mtuci.ru$url"
        }
        val table = XSSFWorkbook(URL(url).openStream())
        for (n in 0 until table.numberOfSheets) {
            processSheet(table.getSheetAt(n))
        }
    } catch (e: Exception) {
        logger.error(e.message, e)
    }
}

private fun processSheet(sheet: Sheet) {
    try {
        val str = sheet.getRow(7).getCell(0).stringCellValue;
        if (!str.contains("Расписание", ignoreCase = true)) {
            logger.info ("Skipping: $str")
            return
        }

        val group = getGroupByName(sheet.getRow(11).getCell(0).stringCellValue)

        logger.info ("Parsing ${group.name}")
        val direction = getDirectionByName(sheet.getRow(10).getCell(0).stringCellValue)

        group.directionId = direction?.id
        val matches = dateRex.findAll(sheet.getRow(12).getCell(0).stringCellValue).map { it.value }.toList()
        matches.firstOrNull()?.let {
            group.termStartDate = dateFormat.parse(it).time
        }
        matches.elementAtOrNull(1)?.let {
            group.termEndDate = dateFormat.parse(it).time
        }

        group.save()

        val rawLessons = mutableListOf<RawRepeatedLesson>()

        for (day in 0..5) for (lessonNum in 0..4) {
            rawLessons.addAll(
                parseRawLessons(sheet.getRow(16 + 6 * day + lessonNum), day),
            )
        }

        val lessons = rawLessons.map {
            it.group = group
            it.buildLesson()
        }

        lessons.forEach {
            val trueLesson = lessonsRepo.findClone(it) ?: it
            group.id?.let { it1 ->
                if (!trueLesson.groupIds.contains(it1)) {
                    trueLesson.groupIds.add(it1)
                }
            }
            trueLesson.save()
        }
    } catch (e: Exception) {
        logger.error(e.message, e)
    }
}

private fun parseRawLessons(row: Row, day: Int): List<RawRepeatedLesson> {

    val lessonNum = try {
        try {
            row.getCell(1).numericCellValue.toInt()
        } catch (_: Exception) {
            row.getCell(1).stringCellValue.toInt()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    //left
    val raw1 = try {
        RawRepeatedLesson(day).apply {
            name = row.getCell(6).stringCellValue.trim()
            teacher = row.getCell(5)?.stringCellValue?.trim()
            type = row.getCell(4)?.stringCellValue?.trim()
            room = row.getCell(3)?.stringCellValue?.trim()
            num = lessonNum
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    //right
    val raw2 = try {
        RawRepeatedLesson(day + 7).apply {
            name = row.getCell(7).stringCellValue.trim()
            teacher = row.getCell(8)?.stringCellValue?.trim()
            type = row.getCell(9)?.stringCellValue?.trim()
            room = row.getCell(10)?.stringCellValue?.trim()
            num = lessonNum
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    return mutableListOf<RawRepeatedLesson>().apply {
        if (raw1?.name?.isNotEmpty() == true) add(raw1)
        if (raw2?.name?.isNotEmpty() == true) add(raw2)
    }
}

private fun getDirectionByName(name: String): Direction? {
    val repo = koin.get<DirectionsRepository>()
    val name = name.substringAfter("Направление").substringBefore("\"").trimIndent()
    return repo.findByName(name)
}

private fun getGroupByName(name: String): Group {
    val trueName = name.replace("Группа", "").trim()
    val repo = koin.get<GroupsRepository>()
    return repo.findByName(trueName) ?: Group().let {
        it.name = trueName
        repo.save(it)
    }
}
