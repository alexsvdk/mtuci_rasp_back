package ru.mtuci.parser.rasp

import org.apache.poi.ss.usermodel.Workbook
import remove
import ru.mtuci.core.RegularLessonsRepository
import save
import java.util.logging.Logger

class RaspParserManager(
    private val parsers: List<RaspParser>,
    private val lessonsRepo: RegularLessonsRepository,
) {
    fun parseRasp(xss: Workbook, logger: Logger) {
        for (sheet in xss) {

            try {

                val res = parsers.firstOrNull { it.canParse(sheet) }?.parse(sheet, logger)

                if (res != null) {
                    logger.info("Найдено ${res.lessons.size} занятий")
                } else {
                    logger.info("Не найден парсер для листа ${sheet.sheetName}")
                    continue
                }

                val lessons = res.lessons
                val group = res.group

                if (group.id != null) {
                    val oldLessons = lessonsRepo.findRegularLessons(group.id).data
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

            }catch (e: Exception){
                logger.info("Ошибка при парсинге листа ${sheet.sheetName}")
                logger.throwing("RaspParserManager", "parseRasp", e)
                e.printStackTrace()
            }
        }
    }
}