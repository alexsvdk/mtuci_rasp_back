package ru.mtuci.parser.mail

import org.apache.commons.compress.archivers.sevenz.SevenZFile
import java.io.File
import java.io.InputStream


class ArchiveManager {

    private val archiveFolder = File("archive")

    init {
        if (!archiveFolder.exists()) {
            archiveFolder.mkdir()
        }
    }

    fun unarchive7Z(inputStream: InputStream, tag: String): List<File> {
        val archiveFile = File(archiveFolder, "$tag.7z")
        if (archiveFile.exists()) {
            archiveFile.delete()
        }
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