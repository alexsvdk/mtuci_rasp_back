package ru.mtuci.ics_backend

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.routing.*
import ru.mtuci.ics_backend.models.IcsRequest

fun main() {

    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
    }.start(wait = true)
}

fun Application.configureRouting() = routing {
    get("/"){
        val request = IcsRequest.fromParameters(call.request.queryParameters)
        if (!request.validate()){
            throw  BadRequestException("No parameters provided")
        }

    }
}