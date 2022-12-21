package ru.mtuci.parser

import ru.mtuci.Config
import ru.mtuci.di.koin
import ru.mtuci.parser.di.parserModule
import ru.mtuci.parser.mail.MailHandler
import java.util.logging.Logger

fun main() {
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