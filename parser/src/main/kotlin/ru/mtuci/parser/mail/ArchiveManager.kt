package ru.mtuci.parser.mail

import org.apache.commons.compress.archivers.sevenz.SevenZFile
import ru.mtuci.Config
import java.io.File
import java.io.InputStream
import java.util.logging.Logger


class ArchiveManager {

    private val archiveFolder = File(Config.APP_BASE_PATH,"archive")
    private val logger = Logger.getLogger(ArchiveManager::class.java.name)

    init {
        if (!archiveFolder.exists()) {
            archiveFolder.mkdirs()
        }
    }

    fun unarchive7Z(inputStream: InputStream, tag: String): List<File> {
        val archiveFile = File(archiveFolder, "$tag.7z")

        logger.info("Unarchive 7z file: ${archiveFile.absolutePath}")

        if (!archiveFile.parentFile.exists())
            archiveFile.parentFile.mkdirs()
        if (!archiveFile.exists())
            archiveFile.createNewFile()

        archiveFile.createNewFile()
        archiveFile.outputStream().use { fileOut ->
            inputStream.copyTo(fileOut)
        }

        val tagFolder = File(archiveFolder, tag)
        if (tagFolder.exists()) {
            tagFolder.deleteRecursively()
        }
        tagFolder.mkdir()

        val zfile = SevenZFile(archiveFile)
        val files = mutableListOf<File>()

        while (true) {
            val entry = zfile.nextEntry ?: break
            val file = File(tagFolder, entry.name)
            if (entry.isDirectory) {
                file.mkdir()
            } else {
                file.createNewFile()
                file.outputStream().use { fileOut ->
                    val content = ByteArray(entry.size.toInt())
                    zfile.read(content, 0, content.size)
                    fileOut.write(content)
                    fileOut.close()
                }
                files.add(file)
            }
        }

        zfile.close()
        return files
    }

    fun deleteTempFiles(tag: String) {
        val tagFolder = File(archiveFolder, tag)
        if (tagFolder.exists()) {
            tagFolder.deleteRecursively()
        }
        val archiveFile = File(archiveFolder, "$tag.7z")
        if (archiveFile.exists()) {
            archiveFile.delete()
        }
    }

}