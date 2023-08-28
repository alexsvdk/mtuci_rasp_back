package ru.mtuci.parser

import org.apache.poi.hssf.usermodel.HSSFWorkbookFactory
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbookFactory
import ru.mtuci.Config
import ru.mtuci.di.koin
import ru.mtuci.parser.di.parserModule
import ru.mtuci.parser.mail.MailHandler
import java.util.logging.Logger


@Suppress("UNREACHABLE_CODE")
fun main() {

    WorkbookFactory.addProvider(HSSFWorkbookFactory())
    WorkbookFactory.addProvider(XSSFWorkbookFactory())

    if (Config.MAIL_USERNAME.isBlank() || Config.MAIL_PASSWORD.isBlank()) {
        throw IllegalArgumentException("Mail username or password is empty")
    }

    koin.loadModules(listOf(parserModule))

    val logger = Logger.getLogger("Parser")

    val mailHandler = koin.get<MailHandler>()
    mailHandler.init()

    while (true) {
        try {
            logger.info("Scanning mail...")
            mailHandler.scanMail()
        } catch (e: Exception) {
            e.printStackTrace()
            logger.throwing("App", "main", e)
        } finally {
            Thread.sleep(1000 * 60)
        }
    }
}