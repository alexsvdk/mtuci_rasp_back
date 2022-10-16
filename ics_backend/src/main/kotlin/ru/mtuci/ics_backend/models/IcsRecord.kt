package ru.mtuci.ics_backend.models

import ru.mtuci.models.BaseDocument

data class IcsRecord(
    val fileName: String,
    val requestHash: String,
) : BaseDocument()
