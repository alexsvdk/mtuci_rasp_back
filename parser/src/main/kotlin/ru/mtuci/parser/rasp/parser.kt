package ru.mtuci.parser.rasp

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.jsoup.Jsoup
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

fun parseRasp() {
    try {
        val urls = getRaspUrls()
        if (urls.isNotEmpty()) {
            lessonsRepo.removeAll()
            urls.forEach(::processTable)
        }
    }catch (e: Exception){
        e.printStackTrace()
    }
}

private fun getRaspUrls(): List<String> {
    val page = Jsoup.connect("https://mtuci.ru/time-table/").get()
    val table = page.select("body > div.content > div > div:nth-child(2) > div > li")
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
        e.printStackTrace()
    }
}

private fun processSheet(sheet: Sheet) {
    try {
        val str = sheet.getRow(5).getCell(0).stringCellValue;
        if (!str.contains("Расписание", ignoreCase = true)) {
            println("Skipping: $str")
            return
        }

        val group = getGroupByName(sheet.sheetName)
        val direction = getDirectionByName(sheet.getRow(9).getCell(0).stringCellValue)

        group.directionId = direction.id
        group.semester = sheet.getRow(6).getCell(0).stringCellValue.filter { it.isDigit() }.toIntOrNull()
        val matches = dateRex.findAll(sheet.getRow(8).getCell(0).stringCellValue).map { it.value }.toList()
        matches.firstOrNull()?.let {
            group.termStartDate = dateFormat.parse(it).time
        }
        matches.elementAtOrNull(1)?.let {
            group.termEndDate = dateFormat.parse(it).time
        }

        group.save()


        if (direction.codeName == null) {
            direction.codeName = group.name?.filter { !it.isDigit() }
            direction.save()
        }

        val rawLessons = mutableListOf<RawRepeatedLesson>()

        for (day in 0..5) for (lessonNum in 0..4) {
            rawLessons.addAll(
                parseRawLessons(sheet.getRow(13 + 6 * day + lessonNum), day),
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

        println("Processed: $str")

    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun parseRawLessons(row: Row, day: Int): List<RawRepeatedLesson> {

    //left
    val raw1 = try {
        RawRepeatedLesson(day).apply {
            name = row.getCell(6).stringCellValue.trim()
            teacher = row.getCell(5).stringCellValue.trim()
            type = row.getCell(4).stringCellValue.trim()
            dist = row.getCell(3).stringCellValue.trim() == "дист"
            num = row.getCell(1).numericCellValue.toInt()
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
            dist = row.getCell(10).stringCellValue.trim() == "дист"
            num = row.getCell(11).numericCellValue.toInt()
            sec = true
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
    val repo = koin.get<DirectionsRepository>()

    return repo.findByCode(code) ?: Direction().let {
        it.code = code
        it.name = name.substringAfter(" ")
        repo.save(it)
    }
}

private fun getGroupByName(name: String): Group {
    val repo = koin.get<GroupsRepository>()
    return repo.findByName(name) ?: Group().let {
        it.name = name
        repo.save(it)
    }
}
