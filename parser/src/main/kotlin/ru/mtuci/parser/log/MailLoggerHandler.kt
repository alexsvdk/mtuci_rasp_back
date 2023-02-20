package ru.mtuci.parser.log

import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogRecord

class MailLoggerHandler : Handler() {

    private val stringBuilder = StringBuilder()
    override fun publish(record: LogRecord?) {
        if (record == null) return
        val str: String = if (record.level.intValue() > Level.INFO.intValue()) {
            "<span style=\"color: red\">${record.message}</span>" +
                    (record.thrown?.stackTraceToString()?.let { "<br><pre>$it</pre>" } ?: "") +
                    "<br>"
        } else {
            record.message + "<br>"
        }
        stringBuilder.appendLine(str)
    }

    override fun flush() {}

    override fun close() {
        stringBuilder.clear()
    }

    fun getLog(): String {
        return stringBuilder.toString()
    }

}