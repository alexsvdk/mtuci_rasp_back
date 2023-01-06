package ru.mtuci.models

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import org.bson.codecs.pojo.annotations.BsonIgnore
import ru.mtuci.Config
import ru.mtuci.models.common.BaseDocument

data class CalendarData(
    val searchFilter: SearchFilter,
) : BaseDocument() {

    @GraphQLIgnore
    var filtersRevisionsHash = 0

    @GraphQLIgnore
    var savedFiltersRevisionsHash = 0

    var calculatorVersion = Config.CALCULATOR_VERSION

    @BsonIgnore
    @GraphQLName("iCalUrl")
    @GraphQLDescription("Ссылка на *.ics файл")
    fun getICalFileUrl(): String = "${Config.APP_BASE_URL_ICS}/$id.ics"

}