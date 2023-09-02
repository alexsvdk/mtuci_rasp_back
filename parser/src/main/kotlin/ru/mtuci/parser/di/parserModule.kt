package ru.mtuci.parser.di

import org.koin.dsl.module
import ru.mtuci.parser.mail.MailHandler
import ru.mtuci.parser.rasp.RaspParserManager
import ru.mtuci.parser.rasp.parsers.exam.ExamParser
import ru.mtuci.parser.rasp.parsers.RaspParser
import ru.mtuci.parser.rasp.parsers.v1.RaspParserV1

val parserModule = module {

    single<List<RaspParser<*>>> {
        listOf(
            get<RaspParserV1>(),
            get<ExamParser>(),
        )
    }

    single { RaspParserManager(get(), get(), get()) }

    single { MailHandler(get()) }

    single { RaspParserV1(get(), get()) }
    single { ExamParser(get()) }


}