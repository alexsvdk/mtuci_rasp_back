package ru.mtuci.backend.generator

import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import graphql.scalars.ExtendedScalars
import graphql.schema.GraphQLType
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Component
class CustomSchemaGeneratorHooks : FederatedSchemaGeneratorHooks(emptyList()) {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier as? KClass<*>) {
        LocalDateTime::class -> ExtendedScalars.DateTime
        Long::class -> ExtendedScalars.GraphQLLong
        else -> null
    }
}