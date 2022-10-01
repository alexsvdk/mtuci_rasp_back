package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

class Room : BaseDocument() {

    @GraphQLDescription("Номер аудитории")
    var number: String? = null

    @GraphQLDescription("Этаж аудитории")
    var floor: Int? = null

    @GraphQLDescription("Локация аудитории")
    var location: RoomLocation? = null

}