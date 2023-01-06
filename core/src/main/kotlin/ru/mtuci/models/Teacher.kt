package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import org.bson.codecs.pojo.annotations.BsonIgnore
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

    @get:BsonIgnore
    @delegate:BsonIgnore
    @GraphQLDescription("ФИО")
    val fullName by lazy {
        val sb = StringBuilder()
        sb.append(lastName)
        sb.append(" ")
        sb.append(firstName ?: "$firstNameI.")
        sb.append(" ")
        sb.append(fathersName ?: "$fathersNameI.")
        sb.toString()
    }

    @get:BsonIgnore
    @delegate:BsonIgnore
    @GraphQLDescription("Короткое имя")
    val shortName by lazy {
        val sb = StringBuilder()
        sb.append(lastName)
        firstNameI?.let {
            sb.append(" ")
            sb.append(it)
            sb.append(".")
        }
        fathersNameI?.let {
            sb.append(" ")
            sb.append(it)
            sb.append(".")
        }
        sb.toString()
    }

}