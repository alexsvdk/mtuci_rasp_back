package ru.mtuci.parser.log

import java.util.logging.Handler
import java.util.logging.LogRecord

class MailLoggerHandler : Handler() {

    private val stringBuilder = StringBuilder()
    override fun publish(record: LogRecord?) {
        if (record == null) return
        stringBuilder.appendLine(record.message)
    }

    override fun flush() {}

    override fun close() {
        stringBuilder.clear()
    }

    fun getLog(): String {
        return stringBuilder.toString()
    }

}