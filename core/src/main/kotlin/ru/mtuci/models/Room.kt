package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import ru.mtuci.models.common.BaseDocument
import ru.mtuci.models.common.RevisionDocument
import ru.mtuci.models.common.RevisionDocumentImpl

class Room : BaseDocument(), RevisionDocument by RevisionDocumentImpl() {

    @GraphQLDescription("Номер аудитории")
    var number: String? = null

    @GraphQLDescription("Этаж аудитории")
    var floor: Int? = null

    @GraphQLDescription("Локация аудитории")
    var location: RoomLocation? = null

}