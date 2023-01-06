package ru.mtuci.models.common

class RevisionDocumentImpl : RevisionDocument {
    override var revision: Int = 0
        private set

    override fun incrementRevision() {
        revision++
    }
}
