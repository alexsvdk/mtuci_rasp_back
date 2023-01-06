package ru.mtuci.models

import ru.mtuci.models.common.BaseDocument
import ru.mtuci.models.common.RevisionDocument
import ru.mtuci.models.common.RevisionDocumentImpl

class Discipline : BaseDocument(), RevisionDocument by RevisionDocumentImpl() {
    var name: String? = null
}