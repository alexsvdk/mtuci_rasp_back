package ru.mtuci.parser

import ru.mtuci.di.koin
import ru.mtuci.parser.di.parserModule
import ru.mtuci.parser.mail.MailHandler

fun main() {
    koin.loadModules(listOf(parserModule))
    val mailHandler = koin.get<MailHandler>()
    mailHandler.init()
    mailHandler.scanMail()
}