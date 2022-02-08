package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

class Teacher: BaseDocument(){

    @GraphQLDescription("Имя")
    var firstName: String? = null

    @GraphQLDescription("Инициалы")
    var nameInitials: String? = null

}