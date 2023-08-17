package ru.mtuci.parser.mail

import org.apache.poi.UnsupportedFileFormatException
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.usermodel.WorkbookFactory
import ru.mtuci.Config
import ru.mtuci.parser.log.MailLoggerHandler
import ru.mtuci.parser.rasp.RaspParserManager
import java.util.*
import java.util.logging.Logger
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeUtility


class MailHandler(
    private val raspParserManager: RaspParserManager,
) {

    private val archiveManager = ArchiveManager()
    private val logger = Logger.getLogger("MailLogger")

    private lateinit var store: Store
    private lateinit var inbox: Folder
    private lateinit var session: Session

    fun init() {
        val props = Properties()
        props["mail.store.protocol"] = "imaps"
        props["mail.imaps.host"] = Config.IMAP_HOST
        props["mail.imaps.port"] = Config.IMAP_PORT
        props["mail.imaps.starttls.enable"] = "true"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.smtp.host"] = Config.SMTP_HOST
        props["mail.smtp.port"] = Config.SMTP_PORT
        props["mail.smtp.ssl.enable"] = "true"
        props["mail.smtp.ssl.trust"] = Config.SMTP_HOST

        session = Session.getDefaultInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(Config.MAIL_USERNAME, Config.MAIL_PASSWORD)
            }
        })
        store = session.getStore("imaps")
        store.connect(Config.IMAP_HOST, Config.IMAP_PORT.toInt(), Config.MAIL_USERNAME, Config.MAIL_PASSWORD)
        inbox = store.getFolder("INBOX")
        inbox.open(Folder.READ_WRITE)
    }

    private fun reinit() {
        try {
            if (!store.isConnected) {
                store.isConnected
            }
            if (!inbox.isOpen) {
                inbox.open(Folder.READ_WRITE)
            }
        } catch (e: Exception) {
            init()
        }
    }

    fun scanMail() {
        try {
            if (!store.isConnected || !inbox.isOpen) {
                init()
            }

            val messages = inbox.messages
            for (message in messages) {
                if (!message.isMimeType("multipart/*"))
                    continue

                if (!message.from.any {
                        it.toString().endsWith("@mtuci.ru") || it.toString()
                            .contains("alex.svdk@gmail.com")
                    })

                    continue
                if (message.flags.contains(Flags.Flag.SEEN))
                    continue

                processMessage(message)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logger.throwing("MailHandler", "scanMail", e)
        }
    }

    private fun processMessage(message: Message) {
        val tag = message.hashCode().toString()
        val logger = Logger.getLogger("MailMessageLogger")
        val handler = MailLoggerHandler()
        var raspFound = false

        try {
            val multipart = message.content as Multipart

            logger.addHandler(handler)
            logger.info("Лог парсинга расписания")

            for (i in 0 until multipart.count) {
                val bodyPart = multipart.getBodyPart(i)

                // xlsx
                if (bodyPart.isMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                    || bodyPart.isMimeType("application/vnd.ms-excel")
                ) {
                    try {
                        raspFound = true
                        logger.severe("---")
                        logger.info("Файл: ${MimeUtility.decodeText(bodyPart.fileName)}")
                        val xss = WorkbookFactory.create(bodyPart.inputStream)
                        raspParserManager.parseRasp(bodyPart.fileName, xss, logger)
                        xss.close()
                    } catch (e: Exception) {
                        logger.warning("Не удалось открыть файл")
                    }
                }

                // 7z
                if (bodyPart.isMimeType("application/x-7z-compressed")) {
                    val files = archiveManager.unarchive7Z(bodyPart.inputStream, tag)
                    val xlsxFiles = files.filter { it.name.contains(".xls") }
                    for (file in xlsxFiles) {
                        raspFound = true
                        logger.severe("---")
                        logger.info("Файл: ${file.name}")
                        var xss: Workbook
                        try {
                            xss = WorkbookFactory.create(file)
                        } catch (e: UnsupportedFileFormatException) {
                            logger.warning("Не удалось открыть файл")
                            logger.throwing("MailHandler", "processMessage", e)
                            continue
                        } catch (e: Exception) {
                            logger.warning("Не удалось открыть файл")
                            logger.throwing("MailHandler", "processMessage", e)
                            e.printStackTrace()
                            continue
                        }
                        raspParserManager.parseRasp(file.name, xss, logger)
                        xss.close()
                    }
                }
            }

        } catch (e: Exception) {
            setIsReadMessage(message, false)
            logger.throwing("MailHandler", "processMessage", e)
            e.printStackTrace()
        } finally {
            if (raspFound) {
                sendLog(message, handler.getLog())
            }
            logger.removeHandler(handler)
            handler.close()
            archiveManager.deleteTempFiles(tag)
        }
    }

    private fun sendLog(message: Message, log: String) {
        var transport: Transport? = null
        try {
            reinit()
            val reply = message.reply(false) as MimeMessage
            reply.setFrom(
                InternetAddress.toString(
                    message
                        .getRecipients(Message.RecipientType.TO)
                )
            )
            reply.setContent(
                """
                    <html>
                        <body>
                            <pre>
                                $log
                            </pre>
                        </body>
                """.trimIndent(),
                "text/html; charset=utf-8",
            )
            reply.replyTo = message.replyTo
            reply.addRecipients(Message.RecipientType.TO, message.from)

            transport = session.getTransport("smtp")
            transport.connect(Config.SMTP_HOST, Config.SMTP_PORT.toInt(), Config.MAIL_USERNAME, Config.MAIL_PASSWORD)
            transport.sendMessage(reply, reply.allRecipients)
            saveToSent(reply)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            transport?.close()
        }
    }

    private fun saveToSent(message: Message) {
        try {
            reinit()
            val sentFolder = listOf(
                store.defaultFolder.list().toList(),
                inbox.list().toList(),
            ).flatten().firstOrNull { it.name.contains("sen", true) || it.name.contains("Отправленные", true) }
            sentFolder?.open(Folder.READ_WRITE)
            sentFolder?.appendMessages(arrayOf(message))
            sentFolder?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setIsReadMessage(message: Message, seen: Boolean) {
        try {
            reinit()
            inbox.setFlags(arrayOf(message), Flags(Flags.Flag.SEEN), seen)
        } catch (e: Exception) {
            logger.throwing("MailHandler", "processMessage", e)
            e.printStackTrace()
        }
    }
}