package ru.mtuci

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType


object Config {

    var MONGO_URL = "mongodb://localhost"
    var SMTP_HOST = "smtp.mail.ru"
    var SMTP_PORT = "465"
    var IMAP_HOST = "imap.mail.ru"
    var IMAP_PORT = "993"
    var MAIL_USERNAME = ""
    var MAIL_PASSWORD = ""
    var APP_BASE_PATH = "./"

    init {
        val env = System.getenv()
        this::class.memberProperties
            .filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.isSubtypeOf(String::class.starProjectedType) }
            .filterIsInstance<KMutableProperty<*>>()
            .forEach { prop ->
                if (env.containsKey(prop.name))
                    prop.setter.call(this, env[prop.name])
            }
        if (!isValid()) error("BAD ENV CONFIG")
    }


    private fun isValid(): Boolean = MONGO_URL.isNotEmpty()

}