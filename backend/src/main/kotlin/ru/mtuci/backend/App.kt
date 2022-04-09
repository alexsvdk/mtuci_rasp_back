package ru.mtuci.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
open class Application

fun main() {
    runApplication<Application>()
}