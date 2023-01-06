package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import ru.mtuci.models.common.BaseDocument
import ru.mtuci.models.common.RevisionDocument
import ru.mtuci.models.common.RevisionDocumentImpl

class Teacher : BaseDocument(), RevisionDocument by RevisionDocumentImpl() {

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