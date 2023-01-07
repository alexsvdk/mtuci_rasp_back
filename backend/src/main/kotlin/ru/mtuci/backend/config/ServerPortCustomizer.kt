package ru.mtuci.backend.config

import org.springframework.boot.web.server.ConfigurableWebServerFactory

import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.stereotype.Component
import ru.mtuci.Config


@Component
class ServerPortCustomizer : WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    override fun customize(factory: ConfigurableWebServerFactory) {
        factory.setPort(Config.PORT)
    }
}