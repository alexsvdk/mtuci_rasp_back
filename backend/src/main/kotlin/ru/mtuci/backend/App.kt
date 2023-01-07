package ru.mtuci.backend

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import ru.mtuci.Config
import java.util.*


@SpringBootApplication
open class Application

fun main(args: Array<String>) {
    val app = SpringApplication(Application::class.java)
    app.setDefaultProperties(
        Collections.singletonMap<String, Any>("server.port", Config.PORT)
    )
    app.run(*args)
}