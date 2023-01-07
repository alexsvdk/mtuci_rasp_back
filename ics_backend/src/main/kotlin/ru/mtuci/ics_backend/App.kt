package ru.mtuci.ics_backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.mtuci.Config
import ru.mtuci.core.CalendarDataRepository
import ru.mtuci.di.koin
import ru.mtuci.ics_backend.calculator.IcsCalculator
import ru.mtuci.ics_backend.di.icsModule
import ru.mtuci.ics_backend.storage.IcsStorage

fun main() {
    koin.loadModules(listOf(icsModule))
    embeddedServer(
        Netty,
        port = Config.PORT,
        host = "0.0.0.0",
        module = Application::icsServerModule,
    ).start(wait = true)
}

fun Application.icsServerModule() {
    install(CallLogging)

    val icsStorage = koin.get<IcsStorage>()
    val calculator = koin.get<IcsCalculator>()
    val calendarDataRepository = koin.get<CalendarDataRepository>()

    routing {
        get("/{id}") {
            val id = call.parameters["id"]?.substringBefore(".ics") ?: throw NotFoundException()
            val calendarData = calendarDataRepository.get(id) ?: throw NotFoundException()
            val isValid =
                calendarData.filtersRevisionsHash == calendarData.savedFiltersRevisionsHash && calendarData.calculatorVersion == Config.CALCULATOR_VERSION
            val icsUrl = (if (isValid) icsStorage.getUrlById(id) else null) ?: run {
                val icsFile = calculator.createIcsFile(id)
                val url = icsStorage.uploadIcs(id, icsFile)
                icsFile.delete()
                url
            }
            call.respondRedirect(icsUrl)
        }
    }
}