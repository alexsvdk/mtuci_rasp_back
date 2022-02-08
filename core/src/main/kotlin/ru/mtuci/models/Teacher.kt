package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

class Teacher : BaseDocument() {

    @GraphQLDescription("Фамилия")
    var lastName: String? = null

    @GraphQLDescription("Имя")
    var firstName: String? = null

    @GraphQLDescription("Отчество")
    var fathersName: String? = null

    @GraphQLDescription("Инициал имени")
    var firstNameI: String? = null

    @GraphQLDescription("нициал отчества")
    var fathersNameI: String? = null

}