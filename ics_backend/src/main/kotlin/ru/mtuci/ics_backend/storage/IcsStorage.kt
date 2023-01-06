package ru.mtuci.ics_backend.storage

import java.io.File

interface IcsStorage {

    suspend fun getUrlById(id: String): String?

    suspend fun uploadIcs(id: String, ics: File): String

}