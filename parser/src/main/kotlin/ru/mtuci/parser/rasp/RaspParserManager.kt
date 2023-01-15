package ru.mtuci.parser.rasp

import org.apache.poi.ss.usermodel.Workbook
import remove
import ru.mtuci.calculators.FilterHashCalculator
import ru.mtuci.core.CalendarDataRepository
import ru.mtuci.core.LessonsRepository
import ru.mtuci.parser.rasp.parsers.RaspParser
import save
import java.util.logging.Logger

class RaspParserManager(
    private val parsers: List<RaspParser<*>>,
    private val lessonsRepo: LessonsRepository,
    private val calendarDataRepo: CalendarDataRepository,
    private val filterHashCalculator: FilterHashCalculator,
) {
    fun parseRasp(xss: Workbook, logger: Logger) {
        for (sheet in xss) {

            try {

                val res = parsers.firstOrNull { it.canParse(sheet) }?.parse(sheet, logger)

                if (res != null) {
                    logger.info("Найдено занятий: ${res.lessons.size}")
                } else {
                    logger.info("Не найден парсер для листа ${sheet.sheetName}")
                    continue
                }

                val lessons = res.lessons
                val group = res.group

                val from = res.termStartDate ?: lessons.mapNotNull { it.dateFrom }.minOrNull()
                val to = res.termEndDate ?: lessons.mapNotNull { it.dateTo }.maxOrNull()

                if (group.id != null) {
                    val oldLessons = lessonsRepo.findLessons(
                        groupId = group.id,
                        from = from,
                        to = to,
                    ).data
                    oldLessons.forEach {
                        it.groupIds.remove(group.id)
                        if (it.groupIds.isEmpty()) {
                            it.remove()
                        } else {
                            it.save()
                        }
                    }
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

                res.incAllRevisions()
                calendarDataRepo.findByAnyOf(
                    res.affectedTeachers.mapNotNull { it.id },
                    res.afectedDisciplines.mapNotNull { it.id },
                    res.affectedRooms.mapNotNull { it.id },
                    listOf(group.id!!)
                ).forEach {
                    it.filtersRevisionsHash = filterHashCalculator.getFilterHash(it.searchFilter)
                    it.save()
                }

            } catch (e: Exception) {
                logger.info("Ошибка при парсинге листа ${sheet.sheetName}")
                logger.throwing("RaspParserManager", "parseRasp", e)
                e.printStackTrace()
            }
        }
    }
}