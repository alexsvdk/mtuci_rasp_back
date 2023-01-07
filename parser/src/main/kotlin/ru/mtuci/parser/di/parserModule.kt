package ru.mtuci.parser.di

import org.koin.dsl.module
import ru.mtuci.parser.mail.MailHandler
import ru.mtuci.parser.rasp.RaspParser
import ru.mtuci.parser.rasp.RaspParserManager
import ru.mtuci.parser.rasp.RaspParserV1

val parserModule = module {

    single { RaspParserV1(get(), get(), get(), get()) }

    single<List<RaspParser>> {
        listOf(
            get<RaspParserV1>(),
        )
    }

    single { RaspParserManager(get(), get()) }

    single { MailHandler(get()) }

}