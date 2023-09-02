package ru.mtuci

import kotlin.reflect.KMutableProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType


object Config {

    // db
    var MONGO_URL = "mongodb://localhost"

    // email
    var SMTP_HOST = "smtp.mail.ru"
    var SMTP_PORT = "465"
    var IMAP_HOST = "imap.mail.ru"
    var IMAP_PORT = "993"
    var MAIL_USERNAME = ""
        get() {
            if (field.isEmpty()) {
                throw IllegalStateException("S3_ACCESS_KEY is empty")
            }
            return field
        }
    var MAIL_PASSWORD = ""
        get() {
            if (field.isEmpty()) {
                throw IllegalStateException("S3_ACCESS_KEY is empty")
            }
            return field
        }

    // s3
    var S3_ACCESS_KEY = ""
        get() {
            if (field.isEmpty()) {
                throw IllegalStateException("S3_ACCESS_KEY is empty")
            }
            return field
        }
    var S3_SECRET_KEY = ""
        get() {
            if (field.isEmpty()) {
                throw IllegalStateException("S3_SECRET_KEY is empty")
            }
            return field
        }
    var S3_ENDPOINT = "https://storage.yandexcloud.net"
    var S3_BUCKET = "mtuci"
    var S3_REGION = "ru-central1"

    // app
    var APP_BASE_PATH = "./"
    var APP_BASE_URL = "http://localhost:8080"
    var APP_BASE_URL_ICS = "$APP_BASE_URL/ics"
    var CALCULATOR_VERSION = 6
    var PORT = 8080

    init {
        val env = System.getenv()
        // strings
        this::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.isSubtypeOf(String::class.starProjectedType) }
            .filterIsInstance<KMutableProperty<*>>().forEach { prop ->
                if (env.containsKey(prop.name)) prop.setter.call(this, env[prop.name])
            }

        // ints
        this::class.memberProperties.filter { it.visibility == KVisibility.PUBLIC }
            .filter { it.returnType.isSubtypeOf(Int::class.starProjectedType) }
            .filterIsInstance<KMutableProperty<*>>().forEach { prop ->
                if (env.containsKey(prop.name)) prop.setter.call(this, env[prop.name]?.toInt())
            }
        if (!isValid()) error("BAD ENV CONFIG")
    }


    private fun isValid(): Boolean = MONGO_URL.isNotEmpty()

}