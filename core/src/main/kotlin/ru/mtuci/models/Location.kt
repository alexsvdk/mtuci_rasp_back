package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

enum class RoomLocation {

    @GraphQLDescription("Авиавоторная")
    MOTOR,

    @GraphQLDescription("Народного ополчения")
    NAROD,

}