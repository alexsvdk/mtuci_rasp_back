package ru.mtuci.ics_backend.storage

import ru.mtuci.ics_backend.models.IcsRequest
import java.io.InputStream

interface IcsStorage {

    fun writeFile(request: IcsRequest, stream: InputStream)

    fun deleteFile(request: IcsRequest)

    fun getFileUrl(request: IcsRequest): String?

}