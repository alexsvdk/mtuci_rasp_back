package ru.mtuci.models.lessons

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import ru.mtuci.models.*

class RegularLesson : BaseLesson() {

    @GraphQLDescription("Day in two weeks [0-13]")
    var tweekDay: Int? = null

}
