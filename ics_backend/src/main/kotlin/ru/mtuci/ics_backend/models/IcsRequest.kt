package ru.mtuci.ics_backend.models

import io.ktor.http.*

data class IcsRequest(
    val groupId: String? = null,
    val teacherId: String? = null
) {
    fun validate() = groupId != null || teacherId != null

    companion object {
        fun fromParameters(parameters: Parameters) = IcsRequest(
            groupId = parameters["groupId"],
            teacherId = parameters["teacherId"],
        );
    }
}
